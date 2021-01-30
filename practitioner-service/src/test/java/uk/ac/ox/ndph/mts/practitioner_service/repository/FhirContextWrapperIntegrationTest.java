package uk.ac.ox.ndph.mts.practitioner_service.repository;

import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FhirContextWrapperIntegrationTest {

    @Test
    @Disabled("Can no longer pass URI into executeTransaction; need to work out how to inject the incorrect URI into FhirContextWrapper")
    void testExecution() {
        // Arrange
        // TODO inject this URI into FhirContextWrapper: "http://nonExistentFhirService:8080",
        FhirContextWrapper sut = new FhirContextWrapper();
        Bundle bundle = new Bundle();

        // Act + Assert
        assertThrows(FhirServerResponseException.class, () ->
                sut.executeTransaction(bundle));
    }
}
