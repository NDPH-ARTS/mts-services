package uk.ac.ox.ndph.mts.practitioner_service.client;

import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleDTO;

import java.util.Collection;

/**
 * Interface for client to the REST role-service
 */
public interface RoleServiceClient {

    /**
     * Get the list of roles
     * @return roles list - might be empty
     * @throws RestException on error communicating with server
     */
    Collection<RoleDTO> getRoles() throws RestException;

    /**
     * Return a role by ID or throws
     * @param id role ID to lookup
     * @return role instance
     * @throws RestException on communication error or role not found
     */
    RoleDTO getRole(String id) throws RestException;

    /**
     * Check if a role ID exists
     * @param id ID to check
     * @return true if exists, false if not
     * @throws RestException on communication error only
     */
    boolean roleIdExists(String id) throws RestException;

}
