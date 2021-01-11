package uk.ac.ox.ndph.mts.practitioner_service.repository;

/**
 * Interface for a store of a data model entity
 */
public interface EntityStore<ENTITY> {

    /**
     * save the entity
     * @param entity the entity to store.
     * @return the entity stored id.
     */
    String saveEntity(ENTITY entity);
}
