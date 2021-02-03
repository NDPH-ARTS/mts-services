package uk.ac.ox.ndph.mts.practitioner_service.repository;

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

    /**
     *  Get an entity by id
     * 
     *  @param entity the entity to store.
     *  @return the entity stored id.
     */
    T getEntity(String id);
}
