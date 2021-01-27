package uk.ac.ox.ndph.mts.practitioner_service.repository;

import java.util.ArrayList;
import java.util.List;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.gclient.ICriterion;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.util.BundleUtil;

import static java.util.Collections.addAll;

/**
 * Wrapper around a Fhir Context
 */
@Component
public class FhirContextWrapper {
    private final FhirContext fhirContext;

    @SuppressWarnings("FieldMayBeFinal") // Value injection shouldn't be final
    @Value("${fhir.uri}")
    private String fhirUri = "";

    /**
     * constructor
     */
    public FhirContextWrapper() {
        fhirContext = FhirContext.forR4();
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
    public Bundle executeTransaction(Bundle input) {
        return fhirContext.newRestfulGenericClient(fhirUri).transaction()
                .withBundle(input).execute();
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
     * @param resourceTypeName resource type name
     * @param criterion criterion to filter search results
     * @return the list of resources by type and criterion
     */
    public List<IBaseResource> searchResource(String resourceTypeName, ICriterion<?> criterion){
        var client = fhirContext.newRestfulGenericClient(fhirUri);

        var resultsBundle = client.search()
                .forResource(resourceTypeName)
                .where(criterion)
                .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
                .execute();

        // extract first page
        List<IBaseResource> searchResults = new ArrayList<>(BundleUtil.toListOfResources(fhirContext, resultsBundle));

        // loop on next pages
        while (resultsBundle.getLink().size() > 1){
            resultsBundle =  client.loadPage()
                    .next(resultsBundle)
                    .execute();

            searchResults.addAll(BundleUtil.toListOfResources(fhirContext, resultsBundle));
        }

        return  searchResults;
    }

//    public <T> IBaseResource readWithId(String id, Class<T> classType) {
//        var client = fhirContext.newRestfulGenericClient("");
//
//        return client.read()
//                .resource(classType.getName())
//                .withId(id)
//                .execute();
//    }

//    public Practitioner readPractitionerWithId(String id) {
//        var client = fhirContext.newRestfulGenericClient(fhirUri);
//        return client.read()
//                .resource(Practitioner.class)
//                .withId(id)
//                .execute();
//    }
}
