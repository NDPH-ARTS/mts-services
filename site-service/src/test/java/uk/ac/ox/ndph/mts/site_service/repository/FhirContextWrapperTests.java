package uk.ac.ox.ndph.mts.site_service.repository;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.Organization;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for {FhirContextWrapper} note this should normally not be run because it depends on a particular
 * FHIR API server being up
 */
@Disabled
public class FhirContextWrapperTests {

    private static final String FHIR_URL = "http://localhost:8080"; // danger! dependency on external server

    @Test
    void TestReadById_whenResourceDoesNotExist_returnsNone() {
        // arrange
        final var wrapper = new FhirContextWrapper();
        // act
        // assert
        assertThrows(ResourceNotFoundException.class,() -> wrapper.readById(FHIR_URL, Organization.class, "better-not-exist"));
    }

}
