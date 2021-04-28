package uk.ac.ox.ndph.mts.init_service.repository;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;

/**
 * FhirServerResponseException
 */
public class FhirServerResponseException extends Exception {

    FhirServerResponseException(final String message, BaseServerResponseException ex) {
        super(message, ex);
    }

}
