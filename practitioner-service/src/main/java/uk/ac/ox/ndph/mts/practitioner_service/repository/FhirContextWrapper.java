package uk.ac.ox.ndph.mts.practitioner_service.repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        this.fhirContext = FhirContext.forR4();
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

    public List<IBaseResource> toListOfResources(final Bundle bundle) {
        return new ArrayList<>(BundleUtil.toListOfResources(fhirContext, bundle));
    }

    public List<IBaseResource> getResourcesFrom(final Bundle bundle, final int expectedNumResources) {
        Objects.requireNonNull(bundle, "Bundle must not be null");
        final List<IBaseResource> resources = toListOfResources(bundle);
        if (resources.size() != expectedNumResources) {
            throw new RestException(String.format(FhirRepo.BAD_RESPONSE_SIZE.message(), resources.size()));
        }
        return resources;
    }

    public IBaseResource toSingleResource(final Bundle bundle) {
        final List<IBaseResource> resources = toListOfResources(bundle);
        if (resources.isEmpty()) {
            throw new RestException("Failed to find resources in bundle");
        }
        if (resources.size() > 1) {
            throw new RestException("Bundle has more than one resource");
        }
        return resources.get(0);
    }
}
