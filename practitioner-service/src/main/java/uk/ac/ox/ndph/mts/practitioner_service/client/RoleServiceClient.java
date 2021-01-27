package uk.ac.ox.ndph.mts.practitioner_service.client;

import uk.ac.ox.ndph.mts.practitioner_service.model.RoleDTO;

import java.util.Collection;

/**
 * Interface for client to the REST role-service
 */
public interface RoleServiceClient {

    Collection<RoleDTO> getRoles();

    RoleDTO getRole(String id);

    boolean roleIdExists(String id);

}
