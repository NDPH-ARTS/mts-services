package uk.ac.ox.ndph.mts.practitioner_service.repository;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;

public class FhirServerResponseException extends Exception {

    private final BaseServerResponseException ex;

    FhirServerResponseException(final String message, BaseServerResponseException ex) {
        super(message, ex);
        this.ex = ex;
    }

}
