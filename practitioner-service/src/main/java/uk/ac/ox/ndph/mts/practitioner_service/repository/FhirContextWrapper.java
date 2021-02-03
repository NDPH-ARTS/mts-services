package uk.ac.ox.ndph.mts.practitioner_service.repository;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
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
     * Execute a search bundle transaction on a FHIR endpoint to search for an
     * entity by an exact id match
     *
     * @param uri the FHIR endpoint URI
     * @param id  of the practitioner
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

    public String getUnqualifiedIdPart(final IBaseResource resource) {
        return resource.getIdElement().toUnqualified().getIdPart();
    }
}
