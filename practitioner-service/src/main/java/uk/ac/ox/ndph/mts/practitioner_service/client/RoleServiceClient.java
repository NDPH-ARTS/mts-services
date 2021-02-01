package uk.ac.ox.ndph.mts.practitioner_service.client;

import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

/**
 * Interface for client to the REST role-service
 */
public interface RoleServiceClient {

    /**
     * Check if a role ID exists
     * @param id ID to check
     * @return true if exists, false if not
     * @throws RestException on communication error only
     */
    boolean roleIdExists(String id) throws RestException;

}
