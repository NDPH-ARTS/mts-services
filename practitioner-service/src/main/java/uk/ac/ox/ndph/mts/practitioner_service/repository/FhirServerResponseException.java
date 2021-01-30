package uk.ac.ox.ndph.mts.practitioner_service.repository;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;

public class FhirServerResponseException extends Exception {

    FhirServerResponseException(final String message, BaseServerResponseException ex) {
        super(message, ex);
    }

}
