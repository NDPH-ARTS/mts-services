package uk.ac.ox.ndph.arts.practitioner_service.repository;

import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.util.BundleUtil;
import uk.ac.ox.ndph.arts.practitioner_service.exception.HttpStatusException;
import uk.ac.ox.ndph.arts.practitioner_service.exception.RestException;

/**
 * Implement IFhirRepository using HAPI client sdk and backed up by an Azure
 * FHIR store.
 */
@Component
class AzureFhirRepository implements IFhirRepository {

    final String PRACTITIONER_ENTITY_NAME = "Practitioner";

    private final FhirContext fhirContext;
    private final Logger logger = LoggerFactory.getLogger(AzureFhirRepository.class);

    @Value("${fhir.uri}")
    private String fhirUri = "";

    public AzureFhirRepository() {
        fhirContext = FhirContext.forR4();
    }

    public String savePractitioner(Practitioner practitioner) throws HttpStatusException {
        // Log the request
        logger.info(String.format("request to fhir: %s",
                fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(practitioner)));

        Bundle responseBundle;
        try {
            responseBundle = fhirContext.newRestfulGenericClient(fhirUri).transaction()
                    .withBundle(bundle(practitioner, PRACTITIONER_ENTITY_NAME)).execute();
        } catch (BaseServerResponseException e) {
            logger.warn("error while updating fhir store", e);
            throw new RestException(e.getMessage());
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);

        // Log the response
        logger.info(String.format("response from fhir: %s",
                fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(responseElement)));
        return practitioner.getIdElement().getValue();
    }

    private IBaseResource extractResponseResource(Bundle bundle) throws RestException {
        List<IBaseResource> resp = new ArrayList<>();
        resp.addAll(BundleUtil.toListOfResources(fhirContext, bundle));

        if (resp.size() != 1) {
            logger.info(String.format("expecting reponse size to be 1. was %d", resp.size()));
            throw new RestException(String.format("bad response size from FHIR: %d", resp.size()));

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