package uk.ac.ox.ndph.mts.init_service.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import uk.ac.ox.ndph.mts.init_service.exception.RestException;

@ExtendWith(MockitoExtension.class)
class HapiFhirRepositoryTests {

    @Mock
    private FhirContextWrapper fhirContextWrapper;

    @Captor
    private ArgumentCaptor<Bundle> bundleCaptor;

    private FhirRepository repository;

    @BeforeEach
    void init() {
        this.repository = new HapiFhirRepository(fhirContextWrapper);
    }

    @Test
    void TestHapiRepository_WhenSavePractitioner_SendsBundleWithTransactionType() throws FhirServerResponseException {
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(String.class), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner()));
        var practitioner = new Practitioner();

        repository.savePractitioner(practitioner);

        verify(fhirContextWrapper).executeTransaction(any(String.class), bundleCaptor.capture());
        var value = bundleCaptor.getValue();
        var type = value.getType();
        assertThat(type, equalTo(Bundle.BundleType.TRANSACTION));
    }
    
    @Test
    void TestHapiRepository_WhenContextWrapperThrowsException_ThrowsRestException() throws FhirServerResponseException {
        // Arrange
        FhirServerResponseException exception = new FhirServerResponseException("message", new ResourceNotFoundException("error"));
        when(fhirContextWrapper.executeTransaction(any(String.class), any(Bundle.class))).thenThrow(exception);
        var practitioner = new Practitioner();

        // Act + Assert
        assertThrows(RestException.class, () -> repository.savePractitioner(practitioner));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsMalformedBundle_ThrowsRestException() throws FhirServerResponseException {
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(String.class), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner(), new Practitioner()));
        var practitioner = new Practitioner();

        assertThrows(RestException.class, () -> repository.savePractitioner(practitioner));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsNull_ThrowsRestException() throws FhirServerResponseException {
        when(fhirContextWrapper.executeTransaction(any(String.class), any(Bundle.class))).thenReturn(null);
        var practitioner = new Practitioner();

        assertThrows(RestException.class, () -> repository.savePractitioner(practitioner));
    }



    // PractitionerRole Tests

    @Test
    void TestSavePractitionerRole_WhenSaveEntity_SendsBundleWithTransactionType() throws FhirServerResponseException {
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(String.class), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new PractitionerRole()));
        var entity = new PractitionerRole();

        repository.savePractitionerRole(entity);

        verify(fhirContextWrapper).executeTransaction(any(String.class), bundleCaptor.capture());
        var value = bundleCaptor.getValue();
        var type = value.getType();
        assertThat(type, equalTo(Bundle.BundleType.TRANSACTION));
    }

    @Test
    void TestSavePractitionerRole_WhenSaveEntity_ReturnsCorrectId() throws FhirServerResponseException {
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(String.class), any(Bundle.class))).thenReturn(responseBundle);
        var entity = new PractitionerRole();
        entity.setId("123");
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(entity));

        var value = repository.savePractitionerRole(entity);

        assertThat(value, equalTo("123"));
    }

    @Test
    void TestSavePractitionerRole_WhenExecuteTransactionThrowsException_ThrowsRestException() throws FhirServerResponseException {
        FhirServerResponseException DUMMY_EXCEPTION = new FhirServerResponseException("", null);
        when(fhirContextWrapper.executeTransaction(any(String.class), any(Bundle.class))).thenThrow(DUMMY_EXCEPTION);
        var entity = new PractitionerRole();

        assertThrows(RestException.class, () -> repository.savePractitionerRole(entity));
    }

    @Test
    void TestSavePractitionerRole_WhenExecuteTransactionReturnsEmptyBundle_ThrowsRestException() throws FhirServerResponseException {
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner(), new Practitioner()));
        var entity = new PractitionerRole();

        assertThrows(RestException.class, () -> repository.savePractitionerRole(entity));
    }

    @Test
    void TestSavePractitionerRole_WhenExecuteTransactionReturnsNull_ThrowsRestException() throws FhirServerResponseException {
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(null);
        var entity = new PractitionerRole();

        assertThrows(RestException.class, () -> repository.savePractitionerRole(entity));
    }
    
    @Test
    void TestHapiRepository_WhenSaveOrganization_SendsBundleWithTransactionType() throws FhirServerResponseException {
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Organization()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);

        var organization = new Organization();
        fhirRepository.saveOrganization(organization);

        verify(fhirContextWrapper).executeTransaction(anyString(), bundleCaptor.capture());
        var value = bundleCaptor.getValue();
        var type = value.getType();
        assertThat(type, equalTo(Bundle.BundleType.TRANSACTION));
    }    
    @Test
    void TestHapiRepository_WhenSaveResearchStudy_SendsBundleWithTransactionType() throws FhirServerResponseException {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new ResearchStudy()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);

        var researchStudy = new ResearchStudy();

        // Act
        fhirRepository.saveResearchStudy(researchStudy);

        // Assert
        verify(fhirContextWrapper).executeTransaction(anyString(), bundleCaptor.capture());
        var value = bundleCaptor.getValue();
        var type = value.getType();
        assertThat(type, equalTo(Bundle.BundleType.TRANSACTION));
    }

    @Test
    void TestHapiRepository_WhenContextWrapperThrowsExpected_ThrowsException() throws FhirServerResponseException {
        FhirServerResponseException exception = new FhirServerResponseException("message", new ResourceNotFoundException("error"));
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenThrow(exception);
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var organization = new Organization();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.saveOrganization(organization));
    }

    @Test
    void TestHapiRepository_WhenContextWrapperResearchStudyThrowsExpected_ThrowsRestException() throws FhirServerResponseException {
        FhirServerResponseException exception = new FhirServerResponseException("message", new ResourceNotFoundException("error"));
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenThrow(exception);
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var researchStudy = new ResearchStudy();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.saveResearchStudy(researchStudy));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsMalformedBundleResearchStudy_ThrowsRestException() throws FhirServerResponseException {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new ResearchStudy(), new ResearchStudy()));
        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var researchStudy = new ResearchStudy();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.saveResearchStudy(researchStudy));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsNullResearchStudy_ThrowsRestException() throws FhirServerResponseException {
        // Arrange
        when(fhirContextWrapper.executeTransaction(anyString(), any(Bundle.class))).thenReturn(null);

        var fhirRepository = new HapiFhirRepository(fhirContextWrapper);
        var researchStudy = new ResearchStudy();

        // Act + Assert
        assertThrows(RestException.class, () -> fhirRepository.saveResearchStudy(researchStudy));
    }    
}
