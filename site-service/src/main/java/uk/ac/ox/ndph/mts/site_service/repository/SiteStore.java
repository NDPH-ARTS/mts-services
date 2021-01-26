package uk.ac.ox.ndph.mts.site_service.repository;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.site_service.model.Site;

/**
 * Implement an EntityStore for Site.
 */
@Component
public class SiteStore implements EntityStore<Site> {

    private final FhirRepository repository;
    private final EntityConverter<Site, org.hl7.fhir.r4.model.Organization> converter;
    private final Logger logger = LoggerFactory.getLogger(SiteStore.class);

    /**
     *
     * @param repository - The fhir repository
     * @param converter - a model-entity to fhir-entity converter
     */
    @Autowired
    public SiteStore(FhirRepository repository,
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

        // TODO: Check if the Organization already exists.


        Organization org = converter.convert(entity);
        String orgId = repository.saveOrganization(org);
        org.setId(orgId);
        logger.info(FhirRepo.SAVE_REQUEST.message(), orgId);




        // TODO: Add research study only when needed.
        String researchStudyId = createResearchStudy(org);
        logger.info(FhirRepo.SAVE_REQUEST.message(), researchStudyId);

        return orgId;
    }

    private String createResearchStudy(Organization org) {
        ResearchStudy rs = new ResearchStudy();
        rs.setTitle(org.getName());
        rs.setStatus(ResearchStudy.ResearchStudyStatus.ACTIVE);
        rs.setSponsor(new Reference("Organization/" + org.getId()));
        return repository.saveResearchStudy(rs);
    }
}
