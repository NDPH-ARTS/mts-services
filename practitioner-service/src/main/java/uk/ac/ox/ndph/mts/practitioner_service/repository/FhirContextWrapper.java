package uk.ac.ox.ndph.mts.practitioner_service.repository;

import java.util.ArrayList;
import java.util.List;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.gclient.ICriterion;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.util.BundleUtil;

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
            return fhirContext.newRestfulGenericClient(fhirUri).transaction()
                    .withBundle(input).execute();
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
}
