package uk.ac.ox.ndph.mts.site_service.repository;

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
}