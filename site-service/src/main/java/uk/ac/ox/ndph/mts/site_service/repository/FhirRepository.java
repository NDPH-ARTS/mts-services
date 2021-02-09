package uk.ac.ox.ndph.mts.site_service.repository;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ResearchStudy;

import java.util.List;
import java.util.Optional;

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
     * Find Organization By name from the FHIR store
     *
     * @param name of the organization to search.
     * @return Organization searched by name or none if not found
     */
    Optional<Organization> findOrganizationByName(String name);

    /**
     * Creates a new ResearchStudy resource.
     *
     * @param researchStudy study to create
     * @return ResearchStudy
     */
    String saveResearchStudy(ResearchStudy researchStudy);

    /**
     * Return the list of all organizations. Note this may include organizations that are
     * not {uk.ac.ox.ndph.mts.site_service.model.Site}s - caller must filter.
     *
     * @return all organization instances in the store, might be empty, not null
     */
    List<Organization> findOrganizations();
    
    /**
     * Return an organization by ID, or Optional.none() if not found
     * @param id organization ID to search for
     * @return optional with the organization or none if not found
     */
    Optional<Organization> findOrganizationById(String id);

    /**
     * Return an organizations with given parent ID, or Optional.none() if not found
     * @param id parent organization ID to search for, null is allowed (will return orgs with no parent)
     * @return collection of organizations with the given parent, might be empty
     */
    List<Organization> findOrganizationsByPartOf(String id);

}
