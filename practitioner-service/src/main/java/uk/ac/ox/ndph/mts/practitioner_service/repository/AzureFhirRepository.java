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
import uk.ac.ox.ndph.mts.practitioner_service.Consts;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

/**
 * Implement FhirRepository interface using HAPI client sdk and backed up by
 * a FHIR store.
 */
@Component
class AzureFhirRepository implements FhirRepository {

    private static final String PRACTITIONER_ENTITY_NAME = "Practitioner";

    private final FhirContext fhirContext;
    private final Logger logger = LoggerFactory.getLogger(AzureFhirRepository.class);

    @Value("${fhir.uri}")
    private String fhirUri = "";

    AzureFhirRepository() {
        fhirContext = FhirContext.forR4();
    }

    /**
     * @param practitioner the practitioner to save.
     * @return
     */
    public String savePractitioner(Practitioner practitioner) {
        // Log the request
        logger.info(String.format(Consts.FHIR_REPO_SAVE_PRACTITIONER_LOG.getValue(),
                fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(practitioner)));

        Bundle responseBundle;
        try {
            responseBundle = fhirContext.newRestfulGenericClient(fhirUri).transaction()
                    .withBundle(bundle(practitioner, PRACTITIONER_ENTITY_NAME)).execute();
        } catch (BaseServerResponseException e) {
            logger.warn(Consts.FHIR_REPO_ERROR_UPDATE_LOG.getValue(), e);
            throw new RestException(e.getMessage());
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);

        // Log the response
        logger.info(String.format(Consts.FHIR_REPO_SAVE_RESPONSE_LOG.getValue(),
                fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(responseElement)));
        return practitioner.getIdElement().getValue();
    }

    private IBaseResource extractResponseResource(Bundle bundle) throws RestException {
        List<IBaseResource> resp = new ArrayList<>();
        resp.addAll(BundleUtil.toListOfResources(fhirContext, bundle));

        if (resp.size() != 1) {
            logger.info(String.format(Consts.FHIR_REPO_BAD_RESPONSE_SIZE_LOG.getValue(), resp.size()));
            throw new RestException(String.format(Consts.FHIR_REPO_BAD_RESPONSE_SIZE_LOG.getValue(), resp.size()));

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
