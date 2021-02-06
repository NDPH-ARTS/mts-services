package uk.ac.ox.ndph.mts.fhir_client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Wrapper around a Fhir Context
 */
@Component
public class FhirContextWrapper {
    private final FhirContext fhirContext;
    private IGenericClient client;

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
            return getClient().transaction()
                    .withBundle(input).execute();
        } catch (BaseServerResponseException ex) {
            final String message = Messages.EXECUTING_TRANSACTION_EXCEPTION.formatMessage(fhirUri);
            throw new FhirServerResponseException(message, ex);
        }
    }

    /**
     * Search for resources of specific type without any conditions.
     *
     * @param resourceClass The type of resource to search for.
     * @param allPages Go through all pages to return all matching results.
     * @param <T> The type of resource to search for.
     * @return A list of resources found.
     */
    public <T extends IBaseResource> List<T> search(Class<T> resourceClass,
                                                    boolean allPages) {
        return search(resourceClass, null, allPages);
    }

    /**
     * Search for resources of specific type without any conditions.
     *
     * @param resourceClass The type of resource to search for.
     * @param criterion The conditions to apply with the search query.
     * @param allPages Go through all pages to return all matching results.
     * @param <T> The type of resource to search for.
     * @return A list of resources found.
     */
    public <T extends IBaseResource> List<T> search(Class<T> resourceClass,
                                                    ICriterion<?> criterion,
                                                    boolean allPages) {
        IGenericClient client = getClient();

        IQuery<Bundle> query = client
                .search()
                .forResource(resourceClass)
                .returnBundle(Bundle.class);

        if (criterion != null) {
            query = query.where(criterion);
        }

        Bundle resultsBundle = query.execute();
        List<T> searchResults = BundleUtil.toListOfResourcesOfType(fhirContext, resultsBundle, resourceClass);

        if (allPages) {
            // loop on next pages
            while (resultsBundle.getLink().size() > 1) {
                resultsBundle = client.loadPage()
                        .next(resultsBundle)
                        .execute();

                searchResults.addAll(BundleUtil.toListOfResourcesOfType(fhirContext, resultsBundle, resourceClass));
            }
        }

        return searchResults;
    }

    /**
     * Reads a specific resource by its Id
     *
     * @param resourceClass The type of resource to search for.
     * @param id The id of the resource required.
     * @param <T> The type of resource to search for.
     * @return The resource specified.
     * @throws ResourceNotFoundException When a resource with the type & id isn't found.
     */
    public <T extends IBaseResource> T readById(final Class<T> resourceClass,
                                                final String id) throws ResourceNotFoundException {
        var baseResource = getClient()
                .read()
                .resource(resourceClass)
                .withId(requireNonBlank(id, "id"))
                .execute();

        return resourceClass.cast(baseResource);
    }

    private IGenericClient getClient() {
        if (this.client == null) {
            client = fhirContext.newRestfulGenericClient(requireNonBlank(fhirUri, "fhirUri"));
        }
        return client;
    }

    private String requireNonBlank(String paramValue, String paramName) {
        if (StringUtils.hasText(paramValue)) {
            return paramValue;
        }

        throw new IllegalArgumentException(Messages.STRING_BLANK_EXCEPTION.formatMessage(paramName));
    }

}
