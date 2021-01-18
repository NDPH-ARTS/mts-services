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
<<<<<<< HEAD:site-service/src/main/java/uk/ac/ox/ndph/mts/site_service/repository/AzureFhirRepository.java
import ca.uhn.fhir.util.BundleUtil;
import uk.ac.ox.ndph.mts.site_service.exception.RestException;
=======
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
>>>>>>> origin/main:practitioner-service/src/main/java/uk/ac/ox/ndph/mts/practitioner_service/repository/HapiFhirRepository.java

/**
 * Implement FhirRepository interface using HAPI client sdk and backed up by 
 * a FHIR store.
 */
@Component
public class HapiFhirRepository implements FhirRepository {

<<<<<<< HEAD:site-service/src/main/java/uk/ac/ox/ndph/mts/site_service/repository/AzureFhirRepository.java
    private static final String ORGANIZATION_ENTITY_NAME = "Organization";
    private static final String RESEARCHSTUDY_ENTITY_NAME = "ResearchStudy";
    private static final String INFO_LOG_REQUEST_TO_FHIR = "request to fhir: %s";
    private static final String INFO_LOG_RESPONSE_FROM_FHIR = "response from fhir: %s";
    private static final String ERROR_UPDATE_FHIR = "error while updating fhir store";
    private static final String ERROR_BAD_RESPONSE_SIZE = "bad response size from FHIR: %d";
=======
    private static final String PRACTITIONER_ENTITY_NAME = "Practitioner";
>>>>>>> origin/main:practitioner-service/src/main/java/uk/ac/ox/ndph/mts/practitioner_service/repository/HapiFhirRepository.java

    private final FhirContextWrapper fhirContextWrapper;
    private final Logger logger = LoggerFactory.getLogger(HapiFhirRepository.class);

    @Value("${fhir.uri}")
    private String fhirUri = "";

    HapiFhirRepository(FhirContextWrapper fhirContextWrapper) {
        this.fhirContextWrapper = fhirContextWrapper;
    }

    /**
<<<<<<< HEAD:site-service/src/main/java/uk/ac/ox/ndph/mts/site_service/repository/AzureFhirRepository.java
     * @param organization the organization to save.
     * @return
=======
     * @param practitioner the practitioner to save.
     * @return id of the saved practitioner
>>>>>>> origin/main:practitioner-service/src/main/java/uk/ac/ox/ndph/mts/practitioner_service/repository/HapiFhirRepository.java
     */
    public String saveOrganization(Organization organization) {
        // Log the request
<<<<<<< HEAD:site-service/src/main/java/uk/ac/ox/ndph/mts/site_service/repository/AzureFhirRepository.java
        logger.info(String.format(INFO_LOG_REQUEST_TO_FHIR,
                fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(organization)));

        Bundle responseBundle;
        try {
            responseBundle = fhirContext.newRestfulGenericClient(fhirUri).transaction()
                    .withBundle(bundle(organization, ORGANIZATION_ENTITY_NAME)).execute();
=======
        if (logger.isInfoEnabled()) {
            logger.info(FhirRepo.SAVE_PRACTITIONER.message(),
                fhirContextWrapper.prettyPrint(practitioner));
        }

        Bundle responseBundle;
        try {
            responseBundle = fhirContextWrapper.executeTrasaction(fhirUri, 
                bundle(practitioner, PRACTITIONER_ENTITY_NAME));
>>>>>>> origin/main:practitioner-service/src/main/java/uk/ac/ox/ndph/mts/practitioner_service/repository/HapiFhirRepository.java
        } catch (BaseServerResponseException e) {
            logger.warn(FhirRepo.UPDATE_ERROR.message(), e);
            throw new RestException(e.getMessage(), e);
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);

        // Log the response
<<<<<<< HEAD:site-service/src/main/java/uk/ac/ox/ndph/mts/site_service/repository/AzureFhirRepository.java
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
=======
        if (logger.isInfoEnabled()) {
            logger.info(FhirRepo.SAVE_RESPONSE.message(),
                    fhirContextWrapper.prettyPrint(responseElement));
        }
        return practitioner.getIdElement().getValue();
>>>>>>> origin/main:practitioner-service/src/main/java/uk/ac/ox/ndph/mts/practitioner_service/repository/HapiFhirRepository.java
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
