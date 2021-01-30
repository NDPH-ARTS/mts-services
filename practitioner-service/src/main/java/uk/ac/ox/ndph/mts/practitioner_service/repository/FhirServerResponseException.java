package uk.ac.ox.ndph.mts.practitioner_service.repository;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;

public class FhirServerResponseException extends Exception {

    // Commenting out to suppress the Sonar code smell.
    // The idea is that this exception will eventually expose interesting details
    // about the wrapped runtimeBaseServerResponseException exception.
    // If we haven't found a use for it after a while, throw it away.
    //private final BaseServerResponseException ex;

    FhirServerResponseException(final String message, BaseServerResponseException ex) {
        super(message, ex);
//        this.ex = ex;
    }

}
