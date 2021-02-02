package uk.ac.ox.ndph.mts.practitioner_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

import java.util.Objects;

/**
 * WebFlux WebClient based version of the REST role-service client. Note this is a sync interface only.
 */
@Component
public class WebFluxRoleServiceClient implements RoleServiceClient {

    private final WebClient webClient;

    public WebFluxRoleServiceClient(final WebClient.Builder webClientBuilder,
                                    @Value("${role.service.uri}") String roleServiceUrlBase) {
        this.webClient = webClientBuilder.baseUrl(roleServiceUrlBase).build();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean roleIdExists(String id) {
        Objects.requireNonNull(id, "id must be non-null");
        return webClient.get()
                .uri("/role/{roleId}", id)
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
