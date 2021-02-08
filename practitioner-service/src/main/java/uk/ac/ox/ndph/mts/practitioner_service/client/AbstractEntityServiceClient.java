package uk.ac.ox.ndph.mts.practitioner_service.client;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

import java.util.Objects;

public abstract class AbstractEntityServiceClient implements EntityServiceClient {

    protected WebClient webClient;
    protected String serviceUrlBase;
    protected String serviceExistsRoute;

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean entityIdExists(String id) throws RestException {
        Objects.requireNonNull(id, "id must be non-null");
        return webClient.get()
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
