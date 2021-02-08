package uk.ac.ox.ndph.mts.sample_service.client.role_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.sample_service.client.ClientResponse;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.RoleDTO;
import uk.ac.ox.ndph.mts.sample_service.exception.RestException;

/**
 * Role service client implementation
 */
@Service
public class RoleServiceClientImpl implements RoleServiceClient {

    private final WebClient webClient;

    private final String rolesRoute;

    private static final String SERVICE_NAME = "role-service";

    public RoleServiceClientImpl(final WebClient.Builder webClientBuilder,
                                 @Value("${role.service.url}") String roleServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(roleServiceUrl).build();
        this.rolesRoute = "/roles/{id}";
    }

    /**
     * Get role with permissions by participant role id
     * @param roleId - participant role id
     * @return RoleDTO - role with permissions
     */
    @Override
    public RoleDTO getRolesById(String roleId) {

        return webClient.get().uri(rolesRoute, roleId)
                .header("Content-Type", "application/json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                                        String.format(ClientResponse.CLIENT_ERROR_RESPONSE.message(),
                                                SERVICE_NAME, resp.statusCode(), roleId))))
                .bodyToMono(RoleDTO.class)
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }
}
