package uk.ac.ox.ndph.mts.init_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.init_service.config.AzureTokenService;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.exception.NullEntityException;
import uk.ac.ox.ndph.mts.init_service.model.Entity;

import java.util.ArrayList;
import java.util.List;

public abstract class ServiceInvoker {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceInvoker.class);

    private final WebClient webClient;

    private AzureTokenService azureTokenService;

    protected ServiceInvoker() {
        this.webClient = WebClient.create();
    }

    protected ServiceInvoker(WebClient webClient,
                             AzureTokenService azureTokenservice) {
        this.webClient = webClient;
        this.azureTokenService = azureTokenservice;
    }

    protected abstract String create(Entity entity) throws DependentServiceException;

    protected <R> R sendBlockingPostRequest(String uri, Entity payload, Class<R> responseExpected)
            throws DependentServiceException {

        try {
            return webClient.post()
                    .uri(uri)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .headers(h -> h.setBearerAuth(azureTokenService.getToken()))
                    .body(Mono.just(payload), payload.getClass())
                    .retrieve()
                    .bodyToMono(responseExpected)
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
