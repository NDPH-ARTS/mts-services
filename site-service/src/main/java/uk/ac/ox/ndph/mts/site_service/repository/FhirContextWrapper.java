package uk.ac.ox.ndph.mts.site_service.repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper around a Fhir Context
 */
@Component
public class FhirContextWrapper {
    private final FhirContext fhirContext;

    /**
     * constructor
     */ 
    public FhirContextWrapper() {
        fhirContext = FhirContext.forR4();
    }
    
    /**
     * Pretty print a resource
     * @param resource the resource to print
     * @return the pretty print String of the resource 
     */
    public String prettyPrint(IBaseResource resource) {
        return  fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(resource);
    }

    /**
     * Execute a bundle transaction to a FHIR endpoint
     * @param uri the FHIR endpoint URI
     * @param input the Bundle to save
     * @return the returned Bundle object from FHIR endpoint
     */
    public Bundle executeTransaction(String uri, Bundle input) throws FhirServerResponseException {
        try {
            return fhirContext.newRestfulGenericClient(uri).transaction()
                    .withBundle(input).execute();
        } catch (BaseServerResponseException ex) {
            final String message = String.format(FhirRepo.PROBLEM_EXECUTING_TRANSACTION.message(), uri);
            throw new FhirServerResponseException(message, ex);
        }
    }

    /**
     * Execute a bundle transaction to a FHIR endpoint
     * @param uri the FHIR endpoint URI
     * @param id of the organization to search.
     * @return Organization searched by Id.
     */
    public Organization executeSearchById(String uri, String id) {
        return fhirContext.newRestfulGenericClient(uri).read().resource(Organization.class).withId(id).execute();
    }

    /**
     * Execute a bundle transaction to a FHIR endpoint
     * @param uri the FHIR endpoint URI
     * @param name of the organization to search.
     * @return Organization searched by name.
     */
    public Bundle executeSearchByName(String uri, String name) throws FhirServerResponseException {
        try {
            return fhirContext.newRestfulGenericClient(uri)
                .search()
                .forResource(Organization.class)
                .where(Organization.NAME.matches().value(name))
                .returnBundle(Bundle.class)
                .execute();
        } catch (BaseServerResponseException ex) {
            final String message = String.format(FhirRepo.SEARCH_ERROR.message(), uri);
            throw new FhirServerResponseException(message, ex);
        }
    }

    /**
     * Extracts a list of resources from a bundle
     * @param bundle the bundle to extract
     * @return the list of resources in the bundle
     */
    public List<IBaseResource> toListOfResources(Bundle bundle) {
        return new ArrayList<>(BundleUtil.toListOfResources(fhirContext, bundle));
    }

    /**
     * Typed version of to list of resources
     * @param <T> The resource type to support
     * @param bundle the bundle to extract
     * @param resourceClass the Resource
     * @return the list of IBaseResource in the bundle
     */
    public <T extends IBaseResource> List<T> toListOfResourcesOfType(Bundle bundle, Class<T> resourceClass) {
        return new ArrayList<>(BundleUtil.toListOfResourcesOfType(this.fhirContext, bundle, resourceClass));
    }

    /**
     * Return a search instance which can be configured and executed, returning a single type of resource
     * @param uri FHIR endpoint URI
     * @param resourceClass class of resource to return in bundle
     * @return IQuery Bundle to return in bundle
     */
    public IQuery<Bundle> search(final String uri, final Class<? extends IBaseResource> resourceClass) {
        return fhirContext.newRestfulGenericClient(uri)
                .search()
                .forResource(resourceClass)
                .returnBundle(Bundle.class);
    }

}
