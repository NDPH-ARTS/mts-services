package uk.ac.ox.ndph.mts.site_service.repository;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import uk.ac.ox.ndph.mts.site_service.exception.RestException;

/**
 * Implement FhirRepository interface using HAPI client sdk and backed up by
 * a FHIR store.
 */
@Component
public class HapiFhirRepository implements FhirRepository {

    private static final String ORGANIZATION_ENTITY_NAME = "Organization";
    private static final String RESEARCHSTUDY_ENTITY_NAME = "ResearchStudy";

    private final FhirContextWrapper fhirContextWrapper;
    private final Logger logger = LoggerFactory.getLogger(HapiFhirRepository.class);

    @Value("${fhir.uri}")
    private String fhirUri = "";

    HapiFhirRepository(FhirContextWrapper fhirContextWrapper) {
        this.fhirContextWrapper = fhirContextWrapper;
    }

    /**
     * @param organization the organization to save.
     * @return id of the saved organization
     */
    public String saveOrganization(Organization organization) {
        // Log the request
        logger.info(FhirRepo.SAVE_REQUEST.message(),
                fhirContextWrapper.prettyPrint(organization));

        Bundle responseBundle;
        try {
            responseBundle = fhirContextWrapper.executeTrasaction(fhirUri, 
                bundle(organization, ORGANIZATION_ENTITY_NAME));
        } catch (BaseServerResponseException e) {
            logger.warn(FhirRepo.UPDATE_ERROR.message(), e);
            throw new RestException(e.getMessage(), e);
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);

        // Log the response
        logger.info(FhirRepo.SAVE_RESPONSE.message(),
                    fhirContextWrapper.prettyPrint(responseElement));

        return responseElement.getIdElement().getIdPart();
    }

    /**
     * @param researchStudy the researchStudy to save.
     * @return ResearchStudy
     */
    public String saveResearchStudy(ResearchStudy researchStudy) {
        // Log the request

        logger.info(FhirRepo.SAVE_REQUEST.message(),
                    fhirContextWrapper.prettyPrint(researchStudy));

        Bundle responseBundle;
        try {
            responseBundle = fhirContextWrapper.executeTrasaction(fhirUri,
                    bundle(researchStudy, RESEARCHSTUDY_ENTITY_NAME));
        } catch (BaseServerResponseException e) {
            logger.warn(FhirRepo.UPDATE_ERROR.message(), e);
            throw new RestException(e.getMessage(), e);
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);

        // Log the response
        logger.info(FhirRepo.SAVE_RESPONSE.message(),
                    fhirContextWrapper.prettyPrint(responseElement));

        return responseElement.getIdElement().getIdPart();
    }

    private IBaseResource extractResponseResource(Bundle bundle) throws RestException {
        var resp = fhirContextWrapper.toListOfResources(bundle);
        
        if (resp.size() != 1) {
            logger.info(FhirRepo.BAD_RESPONSE_SIZE.message(), resp.size());

            throw new RestException(String.format(
                FhirRepo.BAD_RESPONSE_SIZE.message(), resp.size()));

        }
        return resp.get(0);
    }

    private Bundle bundle(Resource resource, String resourceName) {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.TRANSACTION);

        // Add the site as an entry.
        bundle.addEntry().setFullUrl(resource.getIdElement().getValue()).setResource(resource).getRequest()
                .setUrl(resourceName).setMethod(Bundle.HTTPVerb.POST);
        return bundle;
    }
}