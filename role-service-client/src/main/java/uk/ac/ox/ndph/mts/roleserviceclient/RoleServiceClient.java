package uk.ac.ox.ndph.mts.roleserviceclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import uk.ac.ox.ndph.mts.roleserviceclient.exception.RestException;
import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RolePageImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class RoleServiceClient {

    private final WebClient webClient;

    @Value("${role.service.name}")
    private String serviceName;
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

    private Supplier<Retry> retryPolicy;

    @Autowired
    public RoleServiceClient(WebClient.Builder webClientBuilder,  @Value("${role.service.uri}") String roleServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(roleServiceUrl).build();
        this.retryPolicy = retryPolicy;
    }

    public static Consumer<HttpHeaders> noAuth() {
        return (headers) -> {
        };
    }

    public static Consumer<HttpHeaders> basicAuth(final String username, final String password) {
        return (headers) -> headers.setBasicAuth(username, password);
    }

    public static Consumer<HttpHeaders> bearerAuth(final String token) {
        return (headers) -> headers.setBearerAuth(token);
    }

    public RoleServiceClient(final WebClient.Builder webClientBuilder,
                             final Supplier<Retry> retryPolicy,
                             @Value("${role.service.uri}") String roleServiceUrl) {
        this(webClientBuilder, roleServiceUrl);
        this.retryPolicy = retryPolicy;

    }

    public RoleDTO getById(final String roleId,
                           final Consumer<HttpHeaders> authHeaders) throws RestException {
        Objects.requireNonNull(roleId, ResponseMessages.ID_NOT_NULL);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(serviceGetRole)
                        .queryParam("id", roleId)
                        .build())
                .headers(authHeaders)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(RoleDTO.class)
                .retryWhen(retryPolicy.get())
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }

    public boolean entityIdExists(final String roleId,
                                  final Consumer<HttpHeaders> authHeaders) throws RestException {
        Objects.requireNonNull(roleId, ResponseMessages.ID_NOT_NULL);
        return Boolean.TRUE.equals(webClient.get()
                .uri(serviceExistsRoute, roleId)
                .headers(authHeaders)
                .exchange()
                .flatMap(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.just(false);
                    } else if (clientResponse.statusCode().equals(HttpStatus.OK)) {
                        return Mono.just(true);
                    } else {
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                })
                .retryWhen(retryPolicy.get())
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block());
    }

    public Page<RoleDTO> getPage(final int page, final int size,
                                 final Consumer<HttpHeaders> authHeaders) throws RestException {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(serviceGetPaged)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .headers(authHeaders)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                            ResponseMessages.SERVICE_NAME_STATUS_AND_ARGUMENTS.format(
                                    serviceName, resp.statusCode(),
                                    "page=" + page + "; size=" + size))))
                .bodyToMono(RolePageImpl.class)
                .retryWhen(retryPolicy.get())
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }

    public List<RoleDTO> getRolesByIds(final List<String> roleIds,
                                       final Consumer<HttpHeaders> authHeaders) throws RestException {
        Objects.requireNonNull(roleIds, ResponseMessages.LIST_NOT_NULL);
        final String parsedRoleIds = String.join(",", roleIds);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(serviceRolesByIds)
                        .queryParam("ids", parsedRoleIds)
                        .build())
                .headers(authHeaders)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                            ResponseMessages.SERVICE_NAME_STATUS_AND_ID.format(
                                    serviceName, resp.statusCode(), parsedRoleIds))))
                .bodyToMono(RoleDTO[].class)
                .map(Arrays::asList)
                .retryWhen(retryPolicy.get())
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }

    public RoleDTO createEntity(final RoleDTO role,
                                final Consumer<HttpHeaders> authHeaders) throws RestException {
        Objects.requireNonNull(role, ResponseMessages.ROLE_NOT_NULL);
        return webClient.post()
                .uri(serviceCreateRole, role)
                .headers(authHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(role)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                            ResponseMessages.SERVICE_NAME_STATUS_AND_ARGUMENTS.format(
                                    serviceName, resp.statusCode(), "role=" + role))))
                .bodyToMono(RoleDTO.class)
                .retryWhen(retryPolicy.get())
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }


    public RoleDTO updatePermissions(final String roleId,
                                     final List<PermissionDTO> permissionsDTOs,
                                     final Consumer<HttpHeaders> authHeaders) throws RestException {
        Objects.requireNonNull(roleId, ResponseMessages.ID_NOT_NULL);
        Objects.requireNonNull(permissionsDTOs, ResponseMessages.LIST_NOT_NULL);
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(serviceUpdatePermissions)
                        .queryParam("id", roleId)
                        .build())
                .headers(authHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(permissionsDTOs)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                            ResponseMessages.SERVICE_NAME_STATUS_AND_ARGUMENTS.format(
                                    serviceName,
                                    resp.statusCode(),
                                    "roleId=" + roleId + "; permissions=" + listToString(permissionsDTOs)))))
                .bodyToMono(RoleDTO.class)
                .retryWhen(retryPolicy.get())
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }

    private String listToString(final List<?> list) {
        return list.stream().map(Object::toString).collect(Collectors.joining(", "));
    }

    public List<RoleDTO> createMany(final List<? extends RoleDTO> entities,
                                    final Consumer<HttpHeaders> authHeaders) throws RestException {
        Objects.requireNonNull(entities, ResponseMessages.LIST_NOT_NULL);
        RestException error = null;
        final List<RoleDTO> result = new ArrayList<>();
        for (final RoleDTO role : entities) {
            try {
                result.add(createEntity(role, authHeaders));
            } catch (RestException ex) {
                error = ex;
            }
        }
        if (error != null) {
            throw error;
        }
        return result;
    }

    public boolean entityIdExists(String roleId) {
        Objects.requireNonNull(roleId, ResponseMessages.ID_NOT_NULL);
        return Boolean.TRUE.equals(webClient.get()
                .uri(serviceExistsRoute, roleId)
                .exchange()
                .flatMap(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.just(false);
                    } else if (clientResponse.statusCode().equals(HttpStatus.OK)) {
                        return Mono.just(true);
                    } else {
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                })
                .retryWhen(retryPolicy.get())
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block());
    }

    public List<RoleDTO> getRolesByIds(final List<String> roleIds) {
        Objects.requireNonNull(roleIds, ResponseMessages.LIST_NOT_NULL);
        final String parsedRoleIds = String.join(",", roleIds);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(serviceRolesByIds)
                        .queryParam("ids", parsedRoleIds)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                                ResponseMessages.SERVICE_NAME_STATUS_AND_ID.format(
                                        serviceName, resp.statusCode(), parsedRoleIds))))
                .bodyToMono(RoleDTO[].class)
                .map(Arrays::asList)
                .retryWhen(retryPolicy.get())
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }
}
