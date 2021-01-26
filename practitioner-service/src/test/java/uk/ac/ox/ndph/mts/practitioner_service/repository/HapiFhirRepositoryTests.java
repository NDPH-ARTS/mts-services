package uk.ac.ox.ndph.mts.practitioner_service.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Practitioner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

@ExtendWith(MockitoExtension.class)
class HapiFhirRepositoryTests {

    @Mock
    private FhirContextWrapper fhirContextWrapper;

    @Captor
    private ArgumentCaptor<Bundle> bundleCaptor;

    @Mock
    Logger SILENT_LOGGER;

    @Test
    void TestHapiRepository_WhenSavePractitioner_SendsBundleWithTrasactionType() throws FhirServerResponseException {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var practitioner = new Practitioner();
        
        // Act
        fhirRepository.savePractitioner(practitioner);

        // Assert
        verify(fhirContextWrapper).executeTransaction(anyString(), bundleCaptor.capture());
        var value = bundleCaptor.getValue();
        var type = value.getType();
        assertThat(type, equalTo(Bundle.BundleType.TRANSACTION));
    }

    @Test
    void TestHapiRepository_WhenSavePractitioner_ReturnsCorrectId() throws FhirServerResponseException {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var practitioner = new Practitioner();
        practitioner.setId("123");
        
        // Act
        var value = fhirRepository.savePractitioner(practitioner);

        // Assert
        assertThat(value, equalTo("123"));
    }
    
    @Test
    void TestHapiRepository_WhenContextWrapperThrowsException_ThrowsRestException() throws FhirServerResponseException {
        // Arrange
        FhirServerResponseException exception = new FhirServerResponseException("message", new ResourceNotFoundException("error"));
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenThrow(exception);
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        fhirRepository.setLogger(SILENT_LOGGER);
        var practitioner = new Practitioner();
        
        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.savePractitioner(practitioner));
    }
    
    @Test
    void TestHapiRepository_WhenContextReturnsMalformedBundle_ThrowsRestException() throws FhirServerResponseException {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner(), new Practitioner()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var practitioner = new Practitioner();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.savePractitioner(practitioner));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsNull_ThrowsRestException() throws FhirServerResponseException {
        // Arrange
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(null);

        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var practitioner = new Practitioner();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.savePractitioner(practitioner));
    }
}
