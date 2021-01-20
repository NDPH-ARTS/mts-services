package uk.ac.ox.ndph.mts.site_service.repository;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResearchStudy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.ac.ox.ndph.mts.site_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.site_service.model.Site;

/**
 * Implement an EntityStore for Site.
 */
@Component
public class OrganizationStore implements EntityStore<Site> {


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

        // TODO: Add research study only when needed.
        ResearchStudy researchStudyId = createResearchStudy();
        Organization org = converter.convert(entity);

        org.setPartOf(new Reference(researchStudyId));

        // TODO: Check if the Organization already exists.

        return repository.saveOrganization(org);
    }

    private ResearchStudy createResearchStudy() {
        ResearchStudy rs = new ResearchStudy();
        rs.setStatus(ResearchStudy.ResearchStudyStatus.ACTIVE);
        return repository.saveResearchStudy(rs);
    }
}
