package uk.ac.ox.ndph.mts.practitioner_service.repository;

import java.util.Optional;

import java.util.List;

/**
 * Interface for a store of a data model entity
 */
public interface EntityStore<T> {

    /**
     * save the entity
     * @param entity the entity to store.
     * @return the entity stored id.
     */
    String saveEntity(T entity);

    List<T> findEntitiesByUserIdentity(String userIdentity);

    /**
     *  Get an entity by id
     *
     *  @param id the id of the entity to fetch
     *  @return entity or none() if none found
     */
    Optional<T> getEntity(String id);
}
