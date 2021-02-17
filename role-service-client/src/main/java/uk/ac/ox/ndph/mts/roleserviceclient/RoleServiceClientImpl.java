package uk.ac.ox.ndph.mts.roleserviceclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Service
public class RoleServiceClientImpl extends AbstractEntityServiceClient implements RoleServiceClient {

    private final WebClient webClient;

    @Value("${role.service.name}")
    private String serviceName;

    @Value("${role.service.endpoint.roles}")
    private String rolesRoute;

    public RoleServiceClientImpl(final WebClient.Builder webClientBuilder,
                                 @Value("${role.service.url}") String roleServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(roleServiceUrl).build();
        this.serviceExistsRoute = "/roles/{id}";
    }

    /**
     * Get role with permissions by role ids
     *
     * @param roleIds - role ids
     * @return RoleDTO - role with permissions
     */
    @Override
    public List<RoleDTO> getRolesByIds(List<String> roleIds) {

        String parsedRoleIds = String.join(",", roleIds);

        return webClient.get().uri(uriBuilder ->
                                           uriBuilder
                                                   .path(rolesRoute)
                                                   .queryParam("ids", parsedRoleIds)
                                                   .build())
                .header("Content-Type", "application/json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                                String.format(Response.CLIENT_ERROR_RESPONSE.message(),
                                              serviceName, resp.statusCode(), parsedRoleIds))))
                .bodyToMono(RoleDTO[].class)
                .map(Arrays::asList)
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }
}
