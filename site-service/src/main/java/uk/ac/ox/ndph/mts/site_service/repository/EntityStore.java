package uk.ac.ox.ndph.mts.site_service.repository;

import uk.ac.ox.ndph.mts.site_service.model.Site;

import java.util.List;
import java.util.Optional;

/**
 * Interface for a store of a data model entity
 * @param <K> type of ID for entities
 * @param <T> type of entity
 */
public interface EntityStore<K, T> {

    /**
     * save the entity
     *
     * @param entity the entity to store.
     * @return the entity stored id.
     */
    String saveEntity(T entity);

    List<T> findAll();

    Optional<T> findById(K id);

}
