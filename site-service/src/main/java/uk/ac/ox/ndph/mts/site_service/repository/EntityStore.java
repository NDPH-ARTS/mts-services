package uk.ac.ox.ndph.mts.site_service.repository;

import uk.ac.ox.ndph.mts.site_service.model.Site;

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

    /**
     * Find All Organizations from the FHIR store
     * @return List of Organizations
     */
    List<T> findAll();

    /**
     * Find Organization By ID from the FHIR store
     *
     * @param organizationName of the organization to search.
     * @return Organization searched by name.
     */
    Site findOrganizationByName(String organizationName);
}
