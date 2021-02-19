package uk.ac.ox.ndph.mts.roleserviceclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.roleserviceclient.exception.RestException;
import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.Response;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class RoleServiceClient implements EntityServiceClient {

    private final WebClient webClient;

    @Value("${role.service.name}")
    private String serviceName;

    @Value("${role.service.endpoint.roles}")
    private String baseRolesRoute;

    @Value("${role.service.endpoint.exists}")
    private String serviceExistsRoute;

    @Value("${role.service.endpoint.role}")
    private String serviceGetRole;
    @Value("${role.service.endpoint.paged}")
    private String serviceGetPaged;
    @Value("${role.service.endpoint.roles.by.ids}")
    private String serviceRolesByIds;
    @Value("${role.service.endpoint.roles.create}")
    private String serviceCreateRole;
    @Value("${role.service.endpoint.update.permissions}")
    private String serviceUpdatePermissions;

    public RoleServiceClient(final WebClient.Builder webClientBuilder,
                             @Value("${role.service.url}") String roleServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(roleServiceUrl).build();
    }

    @Override
    public RoleDTO getRoleById(final String roleId) throws RestException {
        Objects.requireNonNull(roleId, "roleId must not be null");
        return webClient.get().uri(uriBuilder -> uriBuilder
                .path(serviceGetRole)
                .queryParam("id", roleId)
                .build())
                .header("Content-Type", "application/json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                                String.format(Response.SERVICE_NAME_STATUS_AND_ID.message(),
                                              serviceName, resp.statusCode(), roleId))))
                .bodyToMono(RoleDTO.class)
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean roleIdExists(final String roleId) throws RestException {
        Objects.requireNonNull(roleId, "roleId must not be null");
        return webClient.get().uri(serviceExistsRoute, roleId)
                .exchange()
                .flatMap(clientResponse -> {
                    if (clientResponse.statusCode().is4xxClientError()) {
                        // TODO (archiem) makes sense for 404, but not other 4xx where I would expect an exception
                        return Mono.just(false);
                    } else if (clientResponse.statusCode().is2xxSuccessful()) {
                        // TODO (archiem) makes sense for 200,
                        // but other 2xx are inconclusive and may deserve an exception
                        return Mono.just(true);
                    } else {
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                }).onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }

    @Override
    public Page<RoleDTO> getPaged(final int page, final int size) throws RestException {
        final ParameterizedTypeReference<Page<RoleDTO>> parameterizedTypeReference =
                new ParameterizedTypeReference<>() { };

        return webClient.get().uri(uriBuilder ->
                                           uriBuilder
                                                   .path(serviceGetPaged)
                                                   .queryParam("page", page)
                                                   .queryParam("size", size)
                                                   .build())
                .header("Content-Type", "application/json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                                String.format(Response.SERVICE_NAME_STATUS_AND_ARGUMENTS.message(),
                                              serviceName, resp.statusCode(),
                                              "page=" + page + "; size=" + size))))
                .bodyToMono(parameterizedTypeReference)
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }

    @Override
    public List<RoleDTO> getRolesByIds(final List<String> roleIds) {
        final String parsedRoleIds = String.join(",", roleIds);

        return webClient.get().uri(uriBuilder ->
                                           uriBuilder
                                                   .path(serviceRolesByIds)
                                                   .queryParam("ids", parsedRoleIds)
                                                   .build())
                .header("Content-Type", "application/json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                                String.format(Response.SERVICE_NAME_STATUS_AND_ID.message(),
                                              serviceName, resp.statusCode(), parsedRoleIds))))
                .bodyToMono(RoleDTO[].class)
                .map(Arrays::asList)
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }

    @Override
    public RoleDTO createRole(final RoleDTO role) {
        Objects.requireNonNull(role, "role must not be null");
        return webClient.post().uri(serviceCreateRole, role)
                .header("Content-Type", "application/json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                                String.format(Response.SERVICE_NAME_STATUS_AND_ARGUMENTS.message(),
                                              serviceName, resp.statusCode(), "role=" + role))))
                .bodyToMono(RoleDTO.class)
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }

    @Override
    public RoleDTO updatePermissions(final String roleId, final List<PermissionDTO> permissionsDTOs) {
        Objects.requireNonNull(roleId, "roleId must not be null");
        return webClient.post().uri(uriBuilder ->
                                            uriBuilder.path(serviceUpdatePermissions)
                                                    .queryParam("id", roleId)
                                                    .queryParam("permissionsDTOs", permissionsDTOs)
                                                    .build())
                .header("Content-Type", "application/json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                            String.format(Response.SERVICE_NAME_STATUS_AND_ARGUMENTS.message(),
                                          serviceName,
                                          resp.statusCode(),
                                          "roleId=" + roleId + "; permissions=" + listToString(permissionsDTOs)))))
                .bodyToMono(RoleDTO.class)
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }

    private String listToString(final List<?> list) {
        final StringBuilder sb = new StringBuilder();
        list.forEach(sb::append);
        return sb.toString();
    }

}
