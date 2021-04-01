package uk.ac.ox.ndph.mts.practitioner_service.repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.util.BundleUtil;
import ca.uhn.fhir.rest.gclient.ICriterion;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper around a Fhir Context
 */
@Component
public class FhirContextWrapper {
    private final FhirContext fhirContext;

    @SuppressWarnings("FieldMayBeFinal") // Value injection shouldn't be final
    @Value("${fhir.uri}")
    private String fhirUri = "";

    public FhirContextWrapper() {
        fhirContext = FhirContext.forR4();
    }

    public FhirContextWrapper(FhirContext fhirContext) {
        this.fhirContext = fhirContext;
    }

    /**
     * Execute a search bundle transaction on a FHIR endpoint to search for an
     * entity by an exact id match
     *
     * @param id of the practitioner
     * @return Organization searched by name.
     */
    public Practitioner getById(String id) {
        return fhirContext
                .newRestfulGenericClient(fhirUri)
                .read()
                .resource(Practitioner.class)
                .withId(id)
                .encodedJson()
                .execute();
    }

    /**
     * Pretty print a resource
     *
     * @param resource the resource to print
     * @return the pretty print String of the resource
     */
    public String prettyPrint(IBaseResource resource) {
        return fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(resource);
    }

    /**
     * Execute a bundle transaction to a FHIR endpoint
     *
     * @param input the Bundle to save
     * @return the returned Bundle object from FHIR endpoint
     */
    public Bundle executeTransaction(Bundle input) throws FhirServerResponseException {
        try {
            return fhirContext.newRestfulGenericClient(fhirUri).transaction().withBundle(input).execute();
        } catch (BaseServerResponseException ex) {
            final String message = String.format(FhirRepo.PROBLEM_EXECUTING_TRANSACTION.message(), fhirUri);
            throw new FhirServerResponseException(message, ex);
        }
    }

    /**
     * Extracts a list of resources from a bundle
     *
     * @param bundle the bundle to extract
     * @return the list of resources in the bundle
     */
    public List<IBaseResource> toListOfResources(Bundle bundle) {
        return new ArrayList<>(BundleUtil.toListOfResources(fhirContext, bundle));
    }

    /**
     * Typed version of to list of resources
     *
     * @param <T>           The resource type to support
     * @param bundle        the bundle to extract
     * @param resourceClass the Resource
     * @return the list of IBaseResource in the bundle
     */
    public <T extends IBaseResource> List<T> toListOfResourcesOfType(Bundle bundle, Class<T> resourceClass) {
        return new ArrayList<>(BundleUtil.toListOfResourcesOfType(this.fhirContext, bundle, resourceClass));
    }

    /**
     * Search for a list of resources by type and criterion
     * @param resourceClass resource class to return
     * @param criterion criterion to filter search results
     * @return the list of resources by type and criterion
     */
    public <T extends IBaseResource> List<IBaseResource> searchResource(Class<T> resourceClass,
                                                         ICriterion<?> criterion) {
        var client = fhirContext.newRestfulGenericClient(fhirUri);

        var resultsBundle = client.search()
                .forResource(resourceClass)
                .where(criterion)
                .returnBundle(Bundle.class)
                .execute();

                // extract first page
        List<IBaseResource> searchResults = this.toListOfResources(resultsBundle);

        // loop on next pages
        while (resultsBundle.getLink().size() > 1) {
            resultsBundle =  client.loadPage()
                    .next(resultsBundle)
                    .execute();

            searchResults.addAll(BundleUtil.toListOfResources(fhirContext, resultsBundle));
        }

        return  searchResults;
    }

    /**
     * Return a search instance which can be configured and executed, returning a single type of resource
     *
     * @param uri FHIR endpoint URI
     * @param resourceClass class of resource to return in bundle
     * @return IQuery Bundle to return in bundle
     */
    public IQuery<Bundle> search(final String uri, final Class<? extends IBaseResource> resourceClass) {
        return fhirContext.newRestfulGenericClient(uri)
            .search()
            .forResource(resourceClass)
            .count(50)
            .returnBundle(Bundle.class);
    }
}
