package uk.ac.ox.ndph.mts.site_service.repository;

import org.hl7.fhir.r4.model.Organization;

/**
 * Interface for a FHIR entity repository
 */
public interface FhirRepository {

    /**
     * save a site entity to FHIR store
     *
     * @param site the site to save.
     * @return Nothing.
     */
    String saveSite(Organization site);
}
