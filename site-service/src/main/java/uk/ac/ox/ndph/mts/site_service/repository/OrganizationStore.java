package uk.ac.ox.ndph.mts.site_service.repository;

import ca.uhn.fhir.rest.api.MethodOutcome;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResearchStudy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.ac.ox.ndph.mts.site_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.site_service.helper.FHIRClientHelper;
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
    private FHIRClientHelper fhirClientHelper;

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
        /*
        // TODO: Add research study only when needed.
        ResearchStudy researchStudyId = createResearchStudy();
        Organization org = converter.convert(entity);

        org.setPartOf(new Reference(researchStudyId));

        // TODO: Check if the Organization already exists.
        return repository.saveOrganization(org);
        */
        String id = "";
        String idNew = "";
        String version = "";

        try {
            fhirClientHelper = new FHIRClientHelper("http://localhost:8080");

            Organization org = converter.convert(entity);
            //id = repository.saveOrganization(org);

            MethodOutcome outcome = fhirClientHelper.createResource(org);
            //id = outcome.getId().getIdPart();
            id = outcome.getResource().getIdElement().getIdPart();
            version = "" + (Long.parseLong(outcome.getResource().getIdElement().getVersionIdPart()) + 1);

            Organization orgFound = fhirClientHelper.findOrganizationByID(id);
            //orgFound.setId(new IdType("Organization", id, version));
            orgFound.setId(id);

            ResearchStudy researchStudyId = createResearchStudy();
            researchStudyId.setSponsorTarget(orgFound);
            //orgFound.setPartOf(new Reference(researchStudyId));

            // Updating an Organization
            MethodOutcome outcomeNew = fhirClientHelper.updateResource(orgFound);
            idNew = outcomeNew.getResource().getIdElement().getIdPart();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return id;
    }

    private ResearchStudy createResearchStudy() {
        ResearchStudy rs = new ResearchStudy();
        rs.setStatus(ResearchStudy.ResearchStudyStatus.ACTIVE);
        return repository.saveResearchStudy(rs);
    }
}
