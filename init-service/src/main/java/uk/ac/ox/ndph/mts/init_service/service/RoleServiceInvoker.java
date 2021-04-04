package uk.ac.ox.ndph.mts.init_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.roleserviceclient.ResponseMessages;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;


@Service
public class RoleServiceInvoker {

    private RoleServiceClient roleServiceClient;

    @Autowired
    public RoleServiceInvoker(final RoleServiceClient roleServiceClient) {
        this.roleServiceClient = roleServiceClient;
    }

    public List<RoleDTO> createManyRoles(final List<? extends RoleDTO> entities,
                                         final Consumer<HttpHeaders> authHeaders) throws Exception {
        Objects.requireNonNull(entities, ResponseMessages.LIST_NOT_NULL);
        Exception error = null;
        final List<RoleDTO> result = new ArrayList<>();
        for (final RoleDTO role : entities) {
            try {
                result.add(roleServiceClient.createEntity(role, authHeaders));
            } catch (Exception ex) {
                error = ex;
            }
        }
        if (error != null) {
            throw error;
        }
        return result;
    }
}
