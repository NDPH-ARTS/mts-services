package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

/**
 * Implement FhirRepository interface using HAPI client sdk and backed up by
 * a FHIR store.
 */
@Component
public class HapiFhirRepository implements FhirRepository {

    private static final String PRACTITIONER_ENTITY_NAME = "Practitioner";

    private final FhirContextWrapper fhirContextWrapper;
    private Logger logger = LoggerFactory.getLogger(HapiFhirRepository.class);

    @Value("${fhir.uri}")
    private String fhirUri = "";

    HapiFhirRepository(FhirContextWrapper fhirContextWrapper) {
        this.fhirContextWrapper = fhirContextWrapper;
    }

    /**
     * @param practitioner the practitioner to save.
     * @return id of the saved practitioner
     */
    public String savePractitioner(Practitioner practitioner) {
        logger.info(String.format(
                FhirRepo.SAVE_PRACTITIONER.message(),
                fhirContextWrapper.prettyPrint(practitioner)));

        Bundle responseBundle;
        try {
            responseBundle = fhirContextWrapper.executeTransaction(fhirUri,
                bundle(practitioner, PRACTITIONER_ENTITY_NAME));
        } catch (FhirServerResponseException e) {
            logger.warn(FhirRepo.UPDATE_ERROR.message(), e);
            throw new RestException("Failed to save practitioner", e);
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);

        logger.info(String.format(
                FhirRepo.SAVE_RESPONSE.message(),
                fhirContextWrapper.prettyPrint(responseElement)));
        return practitioner.getIdElement().getValue();
    }
    private IBaseResource extractResponseResource(Bundle bundle) throws RestException {
        var resp = fhirContextWrapper.toListOfResources(bundle);
        
        if (resp.size() != 1) {
            logger.info(String.format(FhirRepo.BAD_RESPONSE_SIZE.message(), resp.size()));
            throw new RestException(String.format(
                FhirRepo.BAD_RESPONSE_SIZE.message(),
                resp.size()));
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

    public void setLogger(final Logger logger) {
        this.logger = logger;
    }
}
