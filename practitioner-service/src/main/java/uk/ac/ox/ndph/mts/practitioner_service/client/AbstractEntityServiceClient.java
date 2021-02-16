package uk.ac.ox.ndph.mts.practitioner_service.client;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

import java.util.Objects;

@Component
public abstract class AbstractEntityServiceClient implements EntityServiceClient {

    protected final WebClient.Builder webClientBuilder;
    protected String serviceUrlBase;
    protected String serviceExistsRoute;

    protected AbstractEntityServiceClient(String serviceUrlBase,
                                          WebClient.Builder webClientBuilder,
                                          String serviceExistsRoute) {
        this.serviceUrlBase = serviceUrlBase;
        this.webClientBuilder = webClientBuilder;
        this.serviceExistsRoute = serviceExistsRoute;
    }

    protected AbstractEntityServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    @LoadBalanced
    public boolean entityIdExists(String id) throws RestException {
        Objects.requireNonNull(id, "id must be non-null");
        return webClientBuilder.baseUrl(serviceUrlBase).build()
                .get()
                .uri(serviceExistsRoute, id)
                .exchange()
                .flatMap(clientResponse -> {
                    if (clientResponse.statusCode().is4xxClientError()) {
                        return Mono.just(false);
                    } else if (clientResponse.statusCode().is2xxSuccessful()) {
                        return Mono.just(true);
                    } else {
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                }).onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }
}
