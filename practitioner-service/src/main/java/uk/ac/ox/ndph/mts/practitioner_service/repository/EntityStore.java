package uk.ac.ox.ndph.mts.practitioner_service.repository;

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


}
