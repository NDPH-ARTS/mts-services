package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FhirContextWrapperIntegrationTest {

    @Test
    public void testExecution() {
        // Arrange
        FhirContextWrapper sut = new FhirContextWrapper();
        Bundle bundle = new Bundle();

        // Act + Assert
        assertThrows(FhirServerResponseException.class, () -> sut.executeTransaction("http://nonExistentFhirService:8080", bundle));
    }
}
