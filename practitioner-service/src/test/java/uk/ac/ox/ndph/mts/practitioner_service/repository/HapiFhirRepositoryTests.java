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
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

@ExtendWith(MockitoExtension.class)
class HapiFhirRepositoryTests {

    @Mock
    private FhirContextWrapper fhirContextWrapper;

    @Captor
    private ArgumentCaptor<Bundle> bundleCaptor;

    @Test
    void TestHapiRepository_WhenSavePractitioner_SendsBundleWithTrasactionType()
    {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTrasaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var practitioner = new Practitioner();
        
        // Act
        fhirRepository.createPractitioner(practitioner);

        // Assert
        verify(fhirContextWrapper).executeTrasaction(anyString(), bundleCaptor.capture());
        var value = bundleCaptor.getValue();
        var type = value.getType();
        assertThat(type, equalTo(Bundle.BundleType.TRANSACTION));
    }

    @Test
    void TestHapiRepository_WhenSavePractitioner_ReturnsCorrectId(){
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTrasaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var practitioner = new Practitioner();
        practitioner.setId("123");
        
        // Act
        var value = fhirRepository.createPractitioner(practitioner);

        // Assert
        assertThat(value, equalTo("123"));
    }
    
    @Test
    void TestHapiRepository_WhenContextWrapperThrowsExpected_ThrowsRestException(){
        when(fhirContextWrapper.executeTrasaction(anyString(), any(Bundle.class))).thenThrow(new ResourceNotFoundException("error"));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var practitioner = new Practitioner();
        
        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.createPractitioner(practitioner));
    }
    
    @Test
    void TestHapiRepository_WhenContextReturnsMalformedBundle_ThrowsRestException(){
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTrasaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner(), new Practitioner()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var practitioner = new Practitioner();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.createPractitioner(practitioner));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsNull_ThrowsRestException(){
        // Arrange
        when(fhirContextWrapper.executeTrasaction(anyString(), any(Bundle.class))).thenReturn(null);

        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var practitioner = new Practitioner();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.createPractitioner(practitioner));
    }
}
