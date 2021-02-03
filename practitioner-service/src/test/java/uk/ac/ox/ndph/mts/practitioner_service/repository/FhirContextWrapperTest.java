package uk.ac.ox.ndph.mts.practitioner_service.repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import org.assertj.core.api.Assertions;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Practitioner;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FhirContextWrapperTest {

    @Test
    void whenTransactionThrowsBaseServerResponseException_thenTranslateToCheckedException() {
        // Arrange
        FhirContext fhirContext = mock(FhirContext.class, RETURNS_DEEP_STUBS);
        final BaseServerResponseException fhirException = mock(BaseServerResponseException.class);
        FhirContextWrapper sut = new FhirContextWrapper(fhirContext);
        final Bundle DUMMY_BUNDLE = new Bundle();

        when(fhirContext.newRestfulGenericClient(anyString()).transaction().withBundle(any(Bundle.class)).execute()).thenThrow(fhirException);


        // Act + Assert
        Assertions.assertThatExceptionOfType(Exception.class)
                .isThrownBy(() -> sut.executeTransaction(DUMMY_BUNDLE))
                .isNotInstanceOf(RuntimeException.class);
    }

    @Test
    void getUnqualifiedIdPart() {
        // Arrange
        FhirContext fhirContext = mock(FhirContext.class);
        FhirContextWrapper sut = new FhirContextWrapper(fhirContext);
        Practitioner resource = new Practitioner();
        resource.setId("345");

        // Act
        String id = sut.getUnqualifiedIdPart(resource);

        // Assert
        assertThat(id).isEqualTo("345");
    }
    
}
