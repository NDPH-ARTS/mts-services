package uk.ac.ox.ndph.mts.roleserviceclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ox.ndph.mts.roleserviceclient.configuration.ClientRoutesConfig;
import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RolePageImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Service
public class RoleServiceClient {

    private final WebClient webClient;

    private final RequestExecutor requestExecutor;

    @Autowired
    public RoleServiceClient(WebClient.Builder webClientBuilder,
                             @Value("${role.service.uri}") String roleServiceUrl,
                             RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
        this.webClient = webClientBuilder.baseUrl(roleServiceUrl).build();
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

    public boolean entityIdExists(final String roleId,
                                  final Consumer<HttpHeaders> authHeaders) {
        Objects.requireNonNull(roleId, ResponseMessages.ID_NOT_NULL);
        try {
            getById(roleId, authHeaders);
        } catch (RuntimeException ex) {
            if (ex.getCause() instanceof WebClientResponseException) {
                WebClientResponseException webClientException = (WebClientResponseException) ex.getCause();
                if (webClientException.getStatusCode() == HttpStatus.NOT_FOUND) {
                    return false;
                }
            }
            throw ex;
        }

        return true;
    }

    public RoleDTO createEntity(final RoleDTO role,
                                final Consumer<HttpHeaders> authHeaders) {
        Objects.requireNonNull(role, ResponseMessages.ROLE_NOT_NULL);
        return requestExecutor.sendBlockingPostRequest(webClient,
                ClientRoutesConfig.getServiceCreateRole(),
                role, RoleDTO.class, authHeaders);
    }

    public RoleDTO getById(final String roleId,
                           final Consumer<HttpHeaders> authHeaders) {
        Objects.requireNonNull(roleId, ResponseMessages.ID_NOT_NULL);
        String uri = UriComponentsBuilder
                .fromUriString(ClientRoutesConfig.getServiceGetRole())
                .build(roleId).toString();
        return requestExecutor.sendBlockingGetRequest(webClient, uri, RoleDTO.class, authHeaders);
    }

    public List<RoleDTO> getRolesByIds(final List<String> roleIds,
                                       final Consumer<HttpHeaders> authHeaders) {
        Objects.requireNonNull(roleIds, ResponseMessages.LIST_NOT_NULL);
        final String parsedRoleIds = String.join(",", roleIds);
        String uri = UriComponentsBuilder
                .fromUriString(ClientRoutesConfig.getServiceRolesByIds())
                .queryParam("ids", parsedRoleIds)
                .build().toString();
        return Arrays.asList(requestExecutor.sendBlockingGetRequest(webClient, uri, RoleDTO[].class, authHeaders));
    }

    public RoleDTO updatePermissions(final String roleId,
                                     final List<PermissionDTO> permissionsDTOs,
                                     final Consumer<HttpHeaders> authHeaders) {
        Objects.requireNonNull(roleId, ResponseMessages.ID_NOT_NULL);
        Objects.requireNonNull(permissionsDTOs, ResponseMessages.LIST_NOT_NULL);
        String uri = UriComponentsBuilder
                .fromUriString(ClientRoutesConfig.getServiceUpdatePermissions())
                .build(roleId).toString();
        return requestExecutor.sendBlockingPostRequest(webClient, uri, permissionsDTOs, RoleDTO.class, authHeaders);
    }

    public Page<RoleDTO> getPage(final int page, final int size,
                                 final Consumer<HttpHeaders> authHeaders) {
        String uri = UriComponentsBuilder
                .fromUriString(ClientRoutesConfig.getServiceGetPaged())
                .queryParam("page", page)
                .queryParam("size", size)
                .build().toString();
        return requestExecutor.sendBlockingGetRequest(webClient, uri, RolePageImpl.class, authHeaders);
    }
}
