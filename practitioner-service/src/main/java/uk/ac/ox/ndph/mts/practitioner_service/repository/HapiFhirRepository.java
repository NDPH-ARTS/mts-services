package uk.ac.ox.ndph.mts.practitioner_service.repository;

import ca.uhn.fhir.model.api.Include;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

import java.util.ArrayList;
import java.util.List;

/**
 * Implement FhirRepository interface using HAPI client sdk and backed up by
 * a FHIR store.
 */
@Component
public class HapiFhirRepository implements FhirRepository {

    private final FhirContextWrapper fhirContextWrapper;
    private final Logger logger = LoggerFactory.getLogger(HapiFhirRepository.class);

    @SuppressWarnings("FieldMayBeFinal") // Value injection shouldn't be final
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
        // Log the request
        if (logger.isInfoEnabled()) {
            logger.info(FhirRepo.SAVE_REQUEST.message(), fhirContextWrapper.prettyPrint(practitioner));
        }

        Bundle responseBundle;
        try {
            responseBundle = fhirContextWrapper.executeTransaction(bundle(practitioner));
        } catch (BaseServerResponseException e) {
            logger.warn(FhirRepo.UPDATE_ERROR.message(), e);
            throw new RestException(e.getMessage(), e);
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);

        // Log the response
        if (logger.isInfoEnabled()) {
            logger.info(FhirRepo.SAVE_RESPONSE.message(), fhirContextWrapper.prettyPrint(responseElement));
        }

        //TODO: replace with the actual id return
        return practitioner.getIdElement().getValue();
    }

    @Override
    public String savePractitionerRole(PractitionerRole practitionerRole) {
        // Log the request
        if (logger.isInfoEnabled()) {
            logger.info(fhirContextWrapper.prettyPrint(practitionerRole));
        }

        Bundle responseBundle;
        try {
            responseBundle = fhirContextWrapper.executeTransaction(bundle(practitionerRole));
        } catch (BaseServerResponseException e) {
            logger.warn(FhirRepo.UPDATE_ERROR.message(), e);
            throw new RestException(e.getMessage(), e);
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);

        // Log the response
        if (logger.isInfoEnabled()) {
            logger.info(FhirRepo.SAVE_RESPONSE.message(), fhirContextWrapper.prettyPrint(responseElement));
        }

        return responseElement.getIdElement().getIdPart();
    }

    @Override
    public List<PractitionerRole> getPractitionerRolesByIdentifier(String identifier) {
        // Log the request
        if (logger.isInfoEnabled()) {
            logger.info(FhirRepo.GET_PRACTITIONER_ROLES_BY_PRACTITIONER_ID.message(), identifier);
        }

        List<PractitionerRole> practitionerRoles = new ArrayList<>();

        var fullTypeName = PractitionerRole.class.getTypeName();
        var lastNotationIndex = fullTypeName.lastIndexOf('.') ;
        var typeName = fullTypeName.substring(lastNotationIndex + 1);

        var results = fhirContextWrapper.searchResourceWithInclude(
                typeName,
                new Include("Practitioner:identifier"),
                PractitionerRole.PRACTITIONER.hasChainedProperty(Practitioner.IDENTIFIER.exactly().identifier(identifier)));

        for (var result: results) {
            practitionerRoles.add((PractitionerRole) result);
        }

        if (logger.isInfoEnabled()) {
            logger.info(FhirRepo.GET_PRACTITIONER_ROLES_BY_PRACTITIONER_ID_RESPONSE.message(), practitionerRoles.size());
        }

        return practitionerRoles;
    }

    private IBaseResource extractResponseResource(Bundle bundle) throws RestException {
        var resp = fhirContextWrapper.toListOfResources(bundle);

        if (resp.size() != 1) {
            logger.info(FhirRepo.BAD_RESPONSE_SIZE.message(), resp.size());
            throw new RestException(String.format(FhirRepo.BAD_RESPONSE_SIZE.message(), resp.size()));
        }
        return resp.get(0);
    }

    private Bundle bundle(Resource resource) {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.TRANSACTION); // we use a single resource bundle, do we need this?

        // Add the resource as an entry.
        bundle.addEntry()
                .setFullUrl(resource.getIdElement().getValue())
                .setResource(resource)
                .getRequest()
                .setUrl(resource.fhirType())
                .setMethod(Bundle.HTTPVerb.POST);
        return bundle;
    }
}
