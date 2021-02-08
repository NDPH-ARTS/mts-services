package uk.ac.ox.ndph.mts.site_service.repository;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.site_service.model.Site;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implement an EntityStore for Site.
 */
@Component
public class SiteStore implements EntityStore<Site, String> {

    private final FhirRepository repository;
    private final EntityConverter<Site, org.hl7.fhir.r4.model.Organization> fromSiteConverter;
    private final EntityConverter<org.hl7.fhir.r4.model.Organization, Site> fromOrgConverter;

    /**
     * @param repository        - The fhir repository
     * @param fromSiteConverter - a model-entity to fhir-entity converter
     * @param fromOrgConverter  - a fhir-entity to model-entity converter
     */
    @Autowired
    public SiteStore(FhirRepository repository,
                     EntityConverter<Site, org.hl7.fhir.r4.model.Organization> fromSiteConverter,
                     EntityConverter<org.hl7.fhir.r4.model.Organization, Site> fromOrgConverter
    ) {
        this.repository = repository;
        this.fromSiteConverter = fromSiteConverter;
        this.fromOrgConverter = fromOrgConverter;
    }

    /**
     * @param entity - The Site entity
     * @return String
     */
    @Override
    public String saveEntity(Site entity) {
        final Organization org = fromSiteConverter.convert(entity);
        final String orgId = repository.saveOrganization(org);
        org.setId(orgId);
        // TODO (who): Add research study only when needed.
        // TODO (alexb): attach research study to site properly
        createResearchStudy(org);
        return orgId;
    }

    /**
     * Find Organization By ID from the FHIR store
     *
     * @param organizationName of the organization to search.
     * @return Organization searched by name.
     */
    @Override
    public Optional<Site> findByName(final String organizationName) {
        return this.repository.findOrganizationByName(organizationName)
                .map(fromOrgConverter::convert);
    }

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
    @Override
    public List<Site> findAll() {
        return this.repository.findOrganizations()
                .stream()
                .map(fromOrgConverter::convert)
                .collect(Collectors.toList());
    }

    /**
     * Find a site entity by ID
     *
     * @return optional with site entity with given ID or none if not found
     */
    @Override
    public Optional<Site> findById(final String id) {
        return this.repository.findOrganizationById(id)
                .map(fromOrgConverter::convert);
    }

    /**
     * Find the root entity.  There should be at most one, or zero for an uninitialized trial.
     *
     * @return entity or empty() if no root present or undefined for this type
     */
    @Override
    public Optional<Site> findRoot() {
        return this.repository.findOrganizationsByPartOf(null)
                .stream()
                .findFirst()
                .map(fromOrgConverter::convert);
    }

}
