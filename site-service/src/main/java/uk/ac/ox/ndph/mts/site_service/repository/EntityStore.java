package uk.ac.ox.ndph.mts.site_service.repository;

import java.util.List;
import java.util.Optional;

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
    String saveEntity(T entity);

    /**
     * Find all entities in the store
     * @return List of entities
     */
    List<T> findAll();

    /**
     * Find entity by ID in the store
     *
     * @param id ID of entity to find
     * @return entity or none() if none found
     */
    Optional<T> findById(K id);

    /**
     * Find the root entity if that makes sense for the entity type.
     * Default implementation just returns empty
     * @return entity or empty() if no root present or undefined for this type
     */
    default Optional<T> findRoot() {
        return Optional.empty();
    }

    /**
     * Check if an entity with the given name exists (if names are valid for this entity)
     * Default implementation always returns false
     * @param entityName entity name to check for, not null
     * @return true if entity with the given name exists
     */
    default boolean existsByName(final String entityName) {
        return false;
    }

}
