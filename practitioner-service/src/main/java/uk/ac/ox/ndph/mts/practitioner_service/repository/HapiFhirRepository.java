package uk.ac.ox.ndph.mts.practitioner_service.repository;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Bundle.HTTPVerb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import java.util.Optional;
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
    public String savePractitioner(final Practitioner practitioner) {
        // Log the request
        if (logger.isInfoEnabled()) {
            logger.info(FhirRepo.SAVE_REQUEST.message(), fhirContextWrapper.prettyPrint(practitioner));
        }

        Bundle responseBundle;
        try {
            responseBundle = fhirContextWrapper.executeTransaction(bundle(practitioner));
        } catch (FhirServerResponseException e) {
            final String message = FhirRepo.FAILED_TO_SAVE_PRACTITIONER.message();
            throw new RestException(message, e);
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);

        // Log the response
        if (logger.isInfoEnabled()) {
            logger.info(FhirRepo.SAVE_RESPONSE.message(), fhirContextWrapper.prettyPrint(responseElement));
        }

        return responseElement.getIdElement().getIdPart();
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
        } catch (FhirServerResponseException e) {
            throw new RestException("Failed to save practitioner role", e);
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);

        // Log the response
        if (logger.isInfoEnabled()) {
            logger.info(FhirRepo.SAVE_RESPONSE.message(), fhirContextWrapper.prettyPrint(responseElement));
        }

        return responseElement.getIdElement().getIdPart();
    }

    /**
     * Search for a fhir practitioner by fhir id
     *
     * @param id of the practitioner to search.
     */
    public Optional<Practitioner> getPractitioner(final String id) {
        try {
            return Optional.of(fhirContextWrapper.getById(id));
        } catch (ResourceNotFoundException ex) {
            return Optional.empty();
        } catch (BaseServerResponseException e) {
            throw new RestException(String.format(FhirRepo.SEARCH_ERROR.message(), e.getMessage()), e);
        }
    }

    @SuppressWarnings("squid:S2629")
    @Override
    public List<PractitionerRole> getPractitionerRolesByUserIdentity(String userIdentity) {
        // Log the request
        logger.info(
                String.format(
                        FhirRepo.GET_PRACTITIONER_ROLES_BY_USER_IDENTITY.message(),
                        userIdentity));

        List<PractitionerRole> practitionerRoles = new ArrayList<>();

        var results = fhirContextWrapper.searchResource(
                PractitionerRole.class,
                PractitionerRole.PRACTITIONER.hasChainedProperty(
                        Practitioner.IDENTIFIER.exactly().identifier(userIdentity)));

        for (var result : results) {
            practitionerRoles.add((PractitionerRole) result);
        }

        logger.info(
                String.format(
                        FhirRepo.GET_PRACTITIONER_ROLES_BY_USER_IDENTITY_RESPONSE.message(),
                        practitionerRoles.size()));

        return practitionerRoles;
    }

    private IBaseResource extractResponseResource(Bundle bundle) throws RestException {
        var resp = fhirContextWrapper.toListOfResources(bundle);

        if (resp.size() != 1) {
            logger.info(String.format(FhirRepo.BAD_RESPONSE_SIZE.message(), resp.size()));
            throw new RestException(String.format(FhirRepo.BAD_RESPONSE_SIZE.message(), resp.size()));
        }
        return resp.get(0);
    }

    private Bundle bundle(Resource resource) {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.TRANSACTION); // we use a single resource bundle, do we need this?

        String url = resource.fhirType();
        HTTPVerb httpMethod = Bundle.HTTPVerb.POST;
        
        if (StringUtils.hasText(resource.getId())) {
            url += "/" + resource.getId();
            httpMethod = Bundle.HTTPVerb.PUT;
        }
        
        bundle.addEntry()
            .setFullUrl(resource.getIdElement().getValue())
            .setResource(resource)
            .getRequest()
            .setUrl(url)
            .setMethod(httpMethod)
            .setIdElement(new StringType(resource.getId()));

        return bundle;
    }
    @Override
    public List<Practitioner> getPractitionersByUserIdentity(String userIdentity) {

        logger.info(
                String.format(
                        FhirRepo.GET_PRACTITIONERS_BY_USER_IDENTITY.message(),
                        userIdentity));

        List<Practitioner> practitioners = new ArrayList<>();

        var results = fhirContextWrapper.searchResource(
                Practitioner.class,
                Practitioner.IDENTIFIER.exactly().identifier(userIdentity));

        for (var result : results) {
            practitioners.add((Practitioner) result);
        }

        logger.info(
                String.format(
                        FhirRepo.GET_PRACTITIONERS_BY_USER_IDENTITY_RESPONSE.message(),
                        practitioners.size()));

        return practitioners;
    }

}
