package uk.ac.ox.ndph.mts.practitioner_service.client;

import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

/**
 * Interface for client to the REST role-service
 */
public interface EntityServiceClient {

    boolean entityIdExists(String id, String token) throws RestException;

}
