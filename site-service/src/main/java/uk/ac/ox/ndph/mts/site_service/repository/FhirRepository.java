package uk.ac.ox.ndph.mts.site_service.repository;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ResearchStudy;

/**
 * Interface for a FHIR entity repository
 */
public interface FhirRepository {

    /**
     * save a site entity to FHIR store
     *
     * @param organization the organization to save.
     * @return Nothing.
     */
    String saveOrganization(Organization organization);

    /**
     * Find Organization By ID from the FHIR store
     *
     * @param name of the organization to search.
     * @return Organization searched by name.
     */
    Organization findOrganizationByName(String name);

    /**
     * Creates a new ResearchStudy resource.
     * @param researchStudy
     * @return ResearchStudy
     */
    String saveResearchStudy(ResearchStudy researchStudy);
}
