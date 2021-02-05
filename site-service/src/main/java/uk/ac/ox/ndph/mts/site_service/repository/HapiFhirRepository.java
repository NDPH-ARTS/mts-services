package uk.ac.ox.ndph.mts.site_service.repository;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.exception.RestException;

import java.util.Collection;

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
     * Return the list of all organizations. Note this may include organizations that are
     * not {uk.ac.ox.ndph.mts.site_service.model.Site}s - caller must filter.
     * @return all organization instances in the store, might be empty, not null
     */
    public Collection<Organization> findOrganizations() {
        try {
            // TODO: filter organizations by the extension element that identifies them as sites
            final Bundle responseBundle = fhirContextWrapper
                    .search(fhirUri, Organization.class)
                    .execute();
            return fhirContextWrapper.toListOfResourcesOfType(responseBundle, Organization.class);
        } catch (BaseServerResponseException e) {
            throw new RestException(String.format(FhirRepo.SEARCH_ERROR.message(), e.getMessage()), e);
        }
    }

    /**
     * @param organization the organization to save.
     * @return id of the saved organization
     */
    public String saveOrganization(Organization organization) {
        // Log the request
        logger.info(FhirRepo.REQUEST_PAYLOAD.message(),
                    fhirContextWrapper.prettyPrint(organization));

        Bundle responseBundle;
        try {
            responseBundle = fhirContextWrapper.executeTransaction(fhirUri,
                bundle(organization, ORGANIZATION_ENTITY_NAME));
        } catch (FhirServerResponseException e) {
            logger.warn(FhirRepo.UPDATE_ERROR.message(), e);
            throw new RestException(FhirRepo.UPDATE_ERROR.message(), e);
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);

        // Log the response
        logger.info(FhirRepo.RESPONSE_PAYLOAD.message(),
                    fhirContextWrapper.prettyPrint(responseElement));

        return responseElement.getIdElement().getIdPart();
    }

    /**
     * @param id of the organization to search.
     * @return id of the saved organization
     */
    public Organization findOrganizationByID(String id) {
        // Log the request
        if (logger.isInfoEnabled()) {
            logger.info(FhirRepo.REQUEST_PAYLOAD.message(), id);
        }
        Organization organization = null;

        try {
            organization = fhirContextWrapper.executeSearchById(fhirUri, id);

        } catch (BaseServerResponseException e) {
            if (logger.isWarnEnabled()) {
                logger.warn(FhirRepo.SEARCH_ERROR.message(), e);
            }
            throw new RestException(e.getMessage(), e);
        }
        return organization;
    }

    /**
     * @param name of the organization to search.
     * @return id of the saved organization
     */
    public Organization findOrganizationByName(String name) {
        // Log the request
        Bundle responseBundle;

        try {
            responseBundle = fhirContextWrapper.executeSearchByName(fhirUri, name);

        } catch (FhirServerResponseException e) {
            logger.warn(FhirRepo.SEARCH_ERROR.message(), e);
            throw new RestException(FhirRepo.SEARCH_ERROR.message(), e);
        }

        if (!responseBundle.getEntry().isEmpty()) {
            return (Organization) responseBundle.getEntry().get(0).getResource();
        }

        return null;
    }

    /**
     * @param researchStudy the researchStudy to save.
     * @return ResearchStudy
     */
    public String saveResearchStudy(ResearchStudy researchStudy) {
        // Log the request
        logger.info(FhirRepo.REQUEST_PAYLOAD.message(),
                    fhirContextWrapper.prettyPrint(researchStudy));

        Bundle responseBundle;
        try {
            responseBundle = fhirContextWrapper.executeTransaction(fhirUri,
                    bundle(researchStudy, RESEARCHSTUDY_ENTITY_NAME));
        } catch (FhirServerResponseException e) {
            logger.warn(FhirRepo.UPDATE_ERROR.message(), e);
            throw new RestException(FhirRepo.UPDATE_ERROR.message(), e);
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);

        // Log the response
        logger.info(FhirRepo.RESPONSE_PAYLOAD.message(),
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
