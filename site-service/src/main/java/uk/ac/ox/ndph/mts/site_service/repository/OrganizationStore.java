package uk.ac.ox.ndph.mts.site_service.repository;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.site_service.model.Site;

/**
 * Implement an EntityStore for Site.
 */
@Component
public class OrganizationStore implements EntityStore<Site> {

    @Value("${fhir.uri}")
    private String fhirUri = "";

    private FhirRepository repository;
    private EntityConverter<Site, org.hl7.fhir.r4.model.Organization> converter;

    /**
     *
     * @param repository - The fhir repository
     * @param converter - a model-entity to fhir-entity converter
     */
    @Autowired
    public OrganizationStore(FhirRepository repository,
            EntityConverter<Site, org.hl7.fhir.r4.model.Organization> converter) {
        this.repository = repository;
        this.converter = converter;
    }

    /**
     *
     * @param entity - The Site entity
     * @return String
     */
    @Override
    public String saveEntity(Site entity) {

        Organization org = converter.convert(entity);
        String orgString = repository.saveOrganization(org);

        // TODO: Check if the Organization already exists.


        // TODO: Add research study only when needed.
        ResearchStudy researchStudyId = createResearchStudy(org);

        return orgString;
    }

    private ResearchStudy createResearchStudy(Organization org) {
        ResearchStudy rs = new ResearchStudy();
        rs.setStatus(ResearchStudy.ResearchStudyStatus.ACTIVE);
        rs.setSponsorTarget(org);
        return repository.saveResearchStudy(rs);
    }
}
