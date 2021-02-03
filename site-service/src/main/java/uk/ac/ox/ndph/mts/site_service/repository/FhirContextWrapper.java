package uk.ac.ox.ndph.mts.site_service.repository;

import java.util.ArrayList;
import java.util.List;

import ca.uhn.fhir.rest.gclient.IQuery;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.util.BundleUtil;

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
    public Bundle executeTransaction(String uri, Bundle input) {
        return fhirContext.newRestfulGenericClient(uri).transaction()
                    .withBundle(input).execute();
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
     */
    public <T extends IBaseResource> List<T> toListOfResourcesOfType(Bundle bundle, Class<T> resourceClass) {
        return new ArrayList<>(BundleUtil.toListOfResourcesOfType(this.fhirContext, bundle, resourceClass));
    }

    /**
     * Return a search instance which can be configured and executed, returning a single type of resource
     * @param uri FHIR endpoint URI
     */
    public IQuery<Bundle> search(final String uri, final String resourceName) {
        return fhirContext.newRestfulGenericClient(uri)
                .search()
                .forResource(resourceName)
                .returnBundle(Bundle.class);
    }

}
