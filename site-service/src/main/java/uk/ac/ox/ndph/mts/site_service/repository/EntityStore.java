package uk.ac.ox.ndph.mts.site_service.repository;

import org.hl7.fhir.r4.model.Organization;

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
     * Find Organization By ID from the FHIR store
     *
     * @param organizationName of the organization to search.
     * @return Organization searched by name.
     */
    Organization findOrganizationByName(String organizationName);
}
