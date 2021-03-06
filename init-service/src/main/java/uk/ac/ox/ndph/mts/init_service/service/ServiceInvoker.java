package uk.ac.ox.ndph.mts.init_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import uk.ac.ox.ndph.mts.init_service.config.AzureTokenService;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.exception.NullEntityException;
import uk.ac.ox.ndph.mts.init_service.model.Entity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public abstract class ServiceInvoker {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceInvoker.class);

    protected WebClient webClient;

    protected String serviceUrlBase;

    protected AzureTokenService azureTokenService;

    public ServiceInvoker(final WebClient.Builder webClientBuilder,
                              String serviceUrlBase,
                              AzureTokenService azureTokenservice) {
        this.serviceUrlBase = serviceUrlBase;
        this.webClient = webClientBuilder.baseUrl(serviceUrlBase).build();
        this.azureTokenService = azureTokenservice;
    }

    // In unit-tests the webclient retry step gets stuck (something about virtual time).
    // This is a quick trick to disable retry while testing without the spring container.
    @Value("${max-webclient-attempts:9}")
    private long maxWebClientAttempts = 0;

    protected abstract String create(Entity entity) throws DependentServiceException;

    protected <R> R sendBlockingPostRequest(String uri, Entity payload, Class<R> responseExpected)
            throws DependentServiceException {
        try {
            LOGGER.debug("About to get Token");
            String token = azureTokenService.getToken();
            LOGGER.debug("Token in invoker - " + token);
            return webClient.post()
                    .uri(uri)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .headers(h -> h.setBearerAuth(token))
                    .body(Mono.just(payload), payload.getClass())
                    .retrieve()
                    .bodyToMono(responseExpected)
                    .retryWhen(Retry.backoff(maxWebClientAttempts,
                            Duration.ofSeconds(5)).maxBackoff(Duration.ofSeconds(30)))
                    .block();

        } catch (Exception e) {
            LOGGER.warn("FAILURE connecting to dependent service {} {}", uri, e.getMessage());
            LOGGER.warn("Exception", e);
            throw new DependentServiceException("FAILURE connecting to " + uri);
        }
    }

    public List<String> execute(List<? extends Entity> entities) throws NullEntityException {
        List<String> entityIds = new ArrayList<>();
        if (entities != null) {
            LOGGER.info("Starting to create {} entity(s)", entities.size());
            for (Entity entity : entities) {
                LOGGER.info("Starting to create {}(s): {}", entity.getClass(), entity);
                entityIds.add(create(entity));
            }
        } else {
            throw new NullEntityException("No entities in payload.");
        }
        return entityIds;
    }
}
