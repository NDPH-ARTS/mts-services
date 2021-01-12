package uk.ac.ox.ndph.mts.site_service.repository;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ResearchStudy;

/**
 * Interface for a FHIR entity repository
 */
public interface FhirRepository {

    /**
     * save a site entity to FHIR store site-service
     *
     * @param organization the organization to save.
     * @return Nothing.
     */
    String saveOrganization(Organization organization);

    /**
     * Creates a new ResearchStudy resource.
     * @param researchStudy
     * @return
     */
    ResearchStudy saveResearchStudy(ResearchStudy researchStudy);
}
