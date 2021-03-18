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
import uk.ac.ox.ndph.mts.roleserviceclient.configuration.ClientRoutesConfig;
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

    private  WebClient webClient;

    private ClientRoutesConfig clientRoutes;

    private Supplier<Retry> retryPolicy;

    @Autowired
    public RoleServiceClient(WebClient.Builder webClientBuilder,
                             @Value("${role.service.uri}") String roleServiceUrl,
                             Supplier<Retry> retryPolicy,
                             ClientRoutesConfig clientRoutes) {
        this.webClient = webClientBuilder.baseUrl(roleServiceUrl).build();
        this.retryPolicy = retryPolicy;
        this.clientRoutes = clientRoutes;
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



    public RoleDTO getById(final String roleId,
                           final Consumer<HttpHeaders> authHeaders) throws RestException {
        Objects.requireNonNull(roleId, ResponseMessages.ID_NOT_NULL);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(clientRoutes.getServiceGetRole())
                        .build(roleId))
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
                .uri(clientRoutes.getServiceExistsRoute(), roleId)
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
                        .path(clientRoutes.getServiceGetPaged())
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
                                    clientRoutes.getServiceName(), resp.statusCode(),
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
                        .path(clientRoutes.getServiceRolesByIds())
                        .build(parsedRoleIds))
                .headers(authHeaders)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                            ResponseMessages.SERVICE_NAME_STATUS_AND_ID.format(
                                    clientRoutes.getServiceName(), resp.statusCode(), parsedRoleIds))))
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
                .uri(clientRoutes.getServiceCreateRole(), role)
                .headers(authHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(role)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                            ResponseMessages.SERVICE_NAME_STATUS_AND_ARGUMENTS.format(
                                    clientRoutes.getServiceName(), resp.statusCode(), "role=" + role))))
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
                .uri(uriBuilder -> uriBuilder.path(clientRoutes.getServiceUpdatePermissions())
                        .build(roleId))
                .headers(authHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(permissionsDTOs)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                            ResponseMessages.SERVICE_NAME_STATUS_AND_ARGUMENTS.format(
                                    clientRoutes.getServiceName(),
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
                .uri(clientRoutes.getServiceExistsRoute(), roleId)
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
                        .path(clientRoutes.getServiceRolesByIds())
                        .build(parsedRoleIds))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                    httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(new RestException(
                                ResponseMessages.SERVICE_NAME_STATUS_AND_ID.format(
                                        clientRoutes.getServiceName(), resp.statusCode(), parsedRoleIds))))
                .bodyToMono(RoleDTO[].class)
                .map(Arrays::asList)
                .retryWhen(retryPolicy.get())
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }
}
