package uk.ac.ox.ndph.mts.practitioner_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.PageableResult;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleDTO;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * WebFlux WebClient based version of the REST role-service client. Note this is a sync interface only.
 */
@Service
public class WebFluxRoleServiceClient implements RoleServiceClient {

    private final WebClient webClient;

    public WebFluxRoleServiceClient(final WebClient.Builder webClientBuilder,
                                    @Value("${role.service.url}") String roleServiceUrlBase) {
        this.webClient = webClientBuilder.baseUrl(roleServiceUrlBase).build();
    }

    private Stream<RoleDTO> roleStream() {
        return webClient.get()
                .uri("/roles?page={page}&size={size}", 0, Integer.MAX_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageableResult<RoleDTO>>() {
                })
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .blockOptional()
                .orElseGet(PageableResult::empty)
                .stream()
                .filter(r -> r != null && r.getId() != null);
    }


    @Override
    public Collection<RoleDTO> getRoles() {
        return roleStream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public RoleDTO getRole(final String id) {
        Objects.requireNonNull(id, "id must be non-null");
        return webClient.get()
                .uri("/role/{roleId}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        response -> Mono.error(new RestException("Role not found: " + id)))
                .bodyToMono(RoleDTO.class)
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
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
