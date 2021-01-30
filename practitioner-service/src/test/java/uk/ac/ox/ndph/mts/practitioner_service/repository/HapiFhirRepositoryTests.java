package uk.ac.ox.ndph.mts.practitioner_service.repository;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void createPractitioner_WhenSavePractitioner_SendsBundleWithTransactionType() {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(responseBundle);
        Practitioner DUMMY_PRACTITIONER = new Practitioner();
        when(fhirContextWrapper.getResourcesFrom(any(Bundle.class), eq(1))).thenReturn(List.of(DUMMY_PRACTITIONER));

        // Act
        repository.createPractitioner(new Practitioner());

        // Assert
        verify(fhirContextWrapper).executeTransaction(bundleCaptor.capture());
        var value = bundleCaptor.getValue();
        var type = value.getType();
        assertThat(type, equalTo(Bundle.BundleType.TRANSACTION));
    }

    @Test
    void createPractitioner_WhenSavePractitioner_ReturnsCorrectId() {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(responseBundle);
        Practitioner RESPONSE_PRACTITIONER = new Practitioner();
        IdType UUID = IdType.newRandomUuid();
        RESPONSE_PRACTITIONER.setIdElement(UUID);
        when(fhirContextWrapper.getResourcesFrom(any(Bundle.class), eq(1))).thenReturn(List.of(RESPONSE_PRACTITIONER));

        // Act
        String practitionerId = repository.createPractitioner(new Practitioner());

        // Assert
        assertThat(practitionerId, equalTo(UUID.getValue()));
    }

    @Test
    void createPractitioner_WhenContextWrapperThrowsExpected_ThrowsRestException() {
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenThrow(new ResourceNotFoundException("error"));
        var practitioner = new Practitioner();

        // Act + Assert
        assertThrows(RestException.class, () -> repository.createPractitioner(practitioner));
    }

    // TODO Are we sure that executeTransaction can return null?
    // If it can't, then this test isn't needed and can be deleted
    // If it can, executeTransaction should return Optional<Bundle> so that client code always knows to handle this.
    // If the handling of a missing Bundle always requires throwing a RestException, then why not put that into executeTransaction?
    @Test
    @Disabled("Not sure that executeTransaction can ever return null")
    void createPractitioner_WhenContextReturnsNull_ThrowsRestException() {
        // Arrange
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(null);
        var practitioner = new Practitioner();

        // Act + Assert
        assertThrows(RestException.class, () -> repository.createPractitioner(practitioner));
    }


    // PractitionerRole Tests

    @Test
    void TestSavePractitionerRole_WhenSaveEntity_SendsBundleWithTransactionType() {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new PractitionerRole()));
        var entity = new PractitionerRole();

        // Act
        repository.savePractitionerRole(entity);

        // Assert
        verify(fhirContextWrapper).executeTransaction(bundleCaptor.capture());
        var value = bundleCaptor.getValue();
        var type = value.getType();
        assertThat(type, equalTo(Bundle.BundleType.TRANSACTION));
    }

    @Test
    void TestSavePractitionerRole_WhenSaveEntity_ReturnsCorrectId() {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(responseBundle);
        var entity = new PractitionerRole();
        entity.setId("123");
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(entity));

        // Act
        var value = repository.savePractitionerRole(entity);

        // Assert
        assertThat(value, equalTo("123"));
    }

    @Test
    void TestSavePractitionerRole_WhenExecuteTransactionThrowsException_ThrowsRestException() {
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenThrow(new ResourceNotFoundException("error"));
        var entity = new PractitionerRole();

        // Act + Assert
        assertThrows(RestException.class, () -> repository.savePractitionerRole(entity));
    }

    @Test
    void TestSavePractitionerRole_WhenExecuteTransactionReturnsEmptyBundle_ThrowsRestException() {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner(), new Practitioner()));
        var entity = new PractitionerRole();

        // Act + Assert
        assertThrows(RestException.class, () -> repository.savePractitionerRole(entity));
    }

    @Test
    void TestSavePractitionerRole_WhenExecuteTransactionReturnsNull_ThrowsRestException() {
        // Arrange
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(null);
        var entity = new PractitionerRole();

        // Act + Assert
        assertThrows(RestException.class, () -> repository.savePractitionerRole(entity));
    }
}
