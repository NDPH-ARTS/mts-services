package uk.ac.ox.ndph.mts.init_service.repository;

/**
 * Interface for a store of a data model entity
 *  @param <T> type of entity
 *  @param <K> type of ID for entities
 */
public interface EntityStore<T, K> {

    /**
     * save the entity
     * @param entity the entity to store.
     * @return the entity stored id.
     */
    K save(T entity);

}
