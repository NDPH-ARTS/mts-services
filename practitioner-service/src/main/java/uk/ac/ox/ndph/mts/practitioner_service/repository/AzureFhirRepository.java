package uk.ac.ox.ndph.mts.practitioner_service.repository;

import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.util.BundleUtil;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

/**
 * Implement FhirRepository interface using HAPI client sdk and backed up by 
 * a FHIR store.
 */
@Component
class AzureFhirRepository implements FhirRepository {

    private final static String PRACTITIONER_ENTITY_NAME = "Practitioner";
    private final static String INFO_LOG_SAVE_PRACTITIONER = "request to fhir: %s";
    private final static String INFO_LOG_RESPONSE_PRACTITIONER = "response from fhir: %s";
    private final static String ERROR_UPDATE_FHIR = "error while updating fhir store";
    private final static String ERROR_BAD_RESPONSE_SIZE = "bad response size from FHIR: %d";

    private final FhirContext fhirContext;
    private final Logger logger = LoggerFactory.getLogger(AzureFhirRepository.class);

    @Value("${fhir.uri}")
    private String fhirUri = "";

    public AzureFhirRepository() {
        fhirContext = FhirContext.forR4();
    }

    public String savePractitioner(Practitioner practitioner){
        // Log the request
        logger.info(String.format(INFO_LOG_SAVE_PRACTITIONER,
                fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(practitioner)));

        Bundle responseBundle;
        try {
            responseBundle = fhirContext.newRestfulGenericClient(fhirUri).transaction()
                    .withBundle(bundle(practitioner, PRACTITIONER_ENTITY_NAME)).execute();
        } catch (BaseServerResponseException e) {
            logger.warn(ERROR_UPDATE_FHIR, e);
            throw new RestException(e.getMessage());
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);

        // Log the response
        logger.info(String.format(INFO_LOG_RESPONSE_PRACTITIONER,
                fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(responseElement)));
        return practitioner.getIdElement().getValue();
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

        // Add the practitioner as an entry.
        bundle.addEntry().setFullUrl(resource.getIdElement().getValue()).setResource(resource).getRequest()
                .setUrl(resourceName).setMethod(Bundle.HTTPVerb.POST);
        return bundle;
    }
}