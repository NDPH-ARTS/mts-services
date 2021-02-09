package uk.ac.ox.ndph.mts.site_service.repository;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
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

import java.util.List;
import java.util.Optional;

/**
 * Implement FhirRepository interface using HAPI client sdk and backed up by
 * a FHIR store.
 */
@Component
public class HapiFhirRepository implements FhirRepository {

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
     *
     * @return all organization instances in the store, might be empty, not null
     */
    public List<Organization> findOrganizations() {
        try {
            // TODO (alexb siteType): filter organizations by the extension element that identifies them as sites
            final Bundle responseBundle = fhirContextWrapper
                    .search(fhirUri, Organization.class)
                    .execute();
            return fhirContextWrapper.toListOfResourcesOfType(responseBundle, Organization.class);
        } catch (BaseServerResponseException e) {
            throw new RestException(String.format(Repository.SEARCH_ERROR.message(), e.getMessage()), e);
        }
    }

    /**
     * Save a single resource of any type to the FHIR API, returning the allocated ID
     *
     * @param resource the resource to create
     * @return id of the created resource
     */
    private String saveResource(final Resource resource) {
        logger.info(Repository.REQUEST_PAYLOAD.message(), fhirContextWrapper.prettyPrint(resource));
        Bundle responseBundle;
        try {
            responseBundle = fhirContextWrapper.executeTransaction(fhirUri,
                    bundle(resource, resource.getResourceType().name()));
        } catch (FhirServerResponseException e) {
            throw new RestException(Repository.UPDATE_ERROR.message(), e);
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);
        logger.info(Repository.RESPONSE_PAYLOAD.message(), fhirContextWrapper.prettyPrint(responseElement));
        return responseElement.getIdElement().getIdPart();
    }

    /**
     * @param organization the organization to save.
     * @return id of the saved organization
     */
    public String saveOrganization(final Organization organization) {
        return saveResource(organization);
    }

    /**
     * Exact search on name
     *
     * @param name of the organization to search.
     * @return org with that name or none() if not found
     */
    @Override
    public Optional<Organization> findOrganizationByName(final String name) {
        try {
            return fhirContextWrapper
                    .search(fhirUri, Organization.class)
                    .where(Organization.NAME.matchesExactly().value(name))
                    .returnBundle(Bundle.class)
                    .execute()
                    .getEntry()
                    .stream()
                    .findFirst()
                    .map(Bundle.BundleEntryComponent::getResource)
                    .map(Organization.class::cast);
        } catch (BaseServerResponseException e) {
            throw new RestException(String.format(Repository.SEARCH_ERROR.message(), e.getMessage()), e);
        }
    }

    /**
     * @param researchStudy the researchStudy to save.
     * @return ResearchStudy
     */
    public String saveResearchStudy(final ResearchStudy researchStudy) {
        return saveResource(researchStudy);
    }

    private IBaseResource extractResponseResource(Bundle bundle) throws RestException {
        final var resp = fhirContextWrapper.toListOfResources(bundle);
        if (resp.size() != 1) {
            throw new RestException(String.format(
                    Repository.BAD_RESPONSE_SIZE.message(), resp.size()));
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

    /**
     * Return an organization by ID in an Optional
     *
     * @param id organization ID to search for
     * @return optional with the organization or empty() if not found
     */
    @Override
    public Optional<Organization> findOrganizationById(final String id) {
        try {
            return Optional.of(fhirContextWrapper.readById(fhirUri, Organization.class, id));
        } catch (ResourceNotFoundException ex) {
            return Optional.empty();
        } catch (BaseServerResponseException e) {
            throw new RestException(String.format(Repository.SEARCH_ERROR.message(), e.getMessage()), e);
        }
    }

    /**
     * Return an organizations with given parent ID, or Optional.none() if not found
     *
     * @param id parent organization ID to search for, null is allowed (will return orgs with no parent)
     * @return collection of organizations with the given parent, might be empty
     */
    @Override
    public List<Organization> findOrganizationsByPartOf(final String id) {
        final var criterion = (id == null)
                ? Organization.PARTOF.isMissing(true)
                : Organization.PARTOF.hasId(id);
        try {
            final Bundle responseBundle = fhirContextWrapper.search(fhirUri, Organization.class)
                    .where(criterion)
                    .returnBundle(Bundle.class)
                    .execute();
            return fhirContextWrapper.toListOfResourcesOfType(responseBundle, Organization.class);
        } catch (BaseServerResponseException e) {
            throw new RestException(String.format(Repository.SEARCH_ERROR.message(), e.getMessage()), e);
        }
    }

}
