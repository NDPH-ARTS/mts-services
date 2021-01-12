package uk.ac.ox.ndph.mts.site_service.repository;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.util.BundleUtil;
import uk.ac.ox.ndph.mts.site_service.exception.RestException;

/**
 * Implement FhirRepository interface using HAPI client sdk and backed up by 
 * a FHIR store.
 */
@Component
class AzureFhirRepository implements FhirRepository {

    private static final String ORGANIZATION_ENTITY_NAME = "Organization";
    private static final String RESEARCHSTUDY_ENTITY_NAME = "ResearchStudy";
    private static final String INFO_LOG_REQUEST_TO_FHIR = "request to fhir: %s";
    private static final String INFO_LOG_RESPONSE_FROM_FHIR = "response from fhir: %s";
    private static final String ERROR_UPDATE_FHIR = "error while updating fhir store";
    private static final String ERROR_BAD_RESPONSE_SIZE = "bad response size from FHIR: %d";

    private final FhirContext fhirContext;
    private final Logger logger = LoggerFactory.getLogger(AzureFhirRepository.class);

    @Value("${fhir.uri}")
    private String fhirUri = "";

    AzureFhirRepository() {
        fhirContext = FhirContext.forR4();
    }

    /**
     * @param organization the organization to save.
     * @return
     */
    public String saveOrganization(Organization organization) {
        // Log the request
        logger.info(String.format(INFO_LOG_REQUEST_TO_FHIR,
                fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(organization)));

        Bundle responseBundle;
        try {
            responseBundle = fhirContext.newRestfulGenericClient(fhirUri).transaction()
                    .withBundle(bundle(organization, ORGANIZATION_ENTITY_NAME)).execute();
        } catch (BaseServerResponseException e) {
            logger.warn(ERROR_UPDATE_FHIR, e);
            throw new RestException(e.getMessage());
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);

        // Log the response
        logger.info(String.format(INFO_LOG_RESPONSE_FROM_FHIR,
                fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(responseElement)));
        return organization.getIdElement().getValue();
    }

    /**
     * @param researchStudy the researchStudy to save.
     * @return
     */
    public ResearchStudy saveResearchStudy(ResearchStudy researchStudy) {
        // Log the request
        logger.info(String.format(INFO_LOG_REQUEST_TO_FHIR,
                fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(researchStudy)));

        Bundle responseBundle;
        try {
            responseBundle = fhirContext.newRestfulGenericClient(fhirUri).transaction()
                    .withBundle(bundle(researchStudy, RESEARCHSTUDY_ENTITY_NAME)).execute();
        } catch (BaseServerResponseException e) {
            logger.warn(ERROR_UPDATE_FHIR, e);
            throw new RestException(e.getMessage());
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);

        // Log the response
        logger.info(String.format(INFO_LOG_RESPONSE_FROM_FHIR,
                fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(responseElement)));
        return researchStudy;
    }

    private IBaseResource extractResponseResource(Bundle bundle) throws RestException {
        List<IBaseResource> resp = new ArrayList<>();
        resp.addAll(BundleUtil.toListOfResources(fhirContext, bundle));

        if (resp.size() != 1) {
            logger.info(String.format(ERROR_BAD_RESPONSE_SIZE, resp.size()));
            throw new RestException(String.format(ERROR_BAD_RESPONSE_SIZE, resp.size()));

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
