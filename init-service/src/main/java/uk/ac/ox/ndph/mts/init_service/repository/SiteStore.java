package uk.ac.ox.ndph.mts.init_service.repository;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.ac.ox.ndph.mts.init_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.init_service.model.SiteDTO;

/**
 * Implement an EntityStore for Site.
 */
@Component
public class SiteStore implements EntityStore<SiteDTO, String> {

    private final FhirRepository repository;
    private EntityConverter<SiteDTO, Organization> fromSiteConverter;

    /**
     * @param repository        - The fhir repository
     * @param fromSiteConverter - a model-entity to fhir-entity converter
     * @param fromOrgConverter  - a fhir-entity to model-entity converter
     */
    @Autowired
    public SiteStore(FhirRepository repository,
                     EntityConverter<SiteDTO, org.hl7.fhir.r4.model.Organization> fromSiteConverter) {
        this.repository = repository;
        this.fromSiteConverter = fromSiteConverter;
    }

    /**
     * @param entity - The Site entity
     * @return String
     */
    @Override
    public String save(SiteDTO entity) {
        final Organization org = fromSiteConverter.convert(entity);
        final String orgId = repository.saveOrganization(org);
        org.setId(orgId);
        createResearchStudy(org);
        return orgId;
    }

    public List<String> saveEntities(List<SiteDTO> sites) {
        return sites.stream().map(s -> save(s)).collect(toList());
    }
    
    /**
     * Check if an entity with the given name exists in the repository
     *
     * @param name to search for
     * @return true if exists already, false otherwise
     */
    // @Override
    // public boolean existsByName(final String name) {
    //     return this.repository.organizationExistsByName(name);
    // }

    private String createResearchStudy(Organization org) {
        ResearchStudy rs = new ResearchStudy();
        rs.setTitle(org.getName());
        rs.setStatus(ResearchStudy.ResearchStudyStatus.ACTIVE);
        rs.setSponsor(new Reference("Organization/" + org.getId()));
        return repository.saveResearchStudy(rs);
    }

    /**
     * This should never actually be empty, but enforce that invariant at the service level not the store level
     *
     * @return list of site entities
     */
    // @Override
    // public List<Site> findAll() {
    //     return this.repository.findOrganizations()
    //             .stream()
    //             .map(fromOrgConverter::convert)
    //             .collect(Collectors.toList());
    // }

    /**
     * Find a site entity by ID
     *
     * @return optional with site entity with given ID or none if not found
     */
    // @Override
    // public Optional<Site> findById(final String id) {
    //     return this.repository.findOrganizationById(id)
    //             .map(fromOrgConverter::convert);
    // }

    /**
     * Find the root entity.  There should be at most one, or zero for an uninitialized trial.
     *
     * @return entity or empty() if no root present or undefined for this type
     */
    // @Override
    // public Optional<Site> findRoot() {
    //     return this.repository.findOrganizationsByPartOf(null)
    //             .stream()
    //             .findFirst()
    //             .map(fromOrgConverter::convert);
    // }

}
