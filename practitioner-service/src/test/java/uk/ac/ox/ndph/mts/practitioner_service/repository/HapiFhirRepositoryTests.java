package uk.ac.ox.ndph.mts.practitioner_service.repository;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HapiFhirRepositoryTests {

    @Mock
    private FhirContextWrapper fhirContextWrapper;

    @Captor
    private ArgumentCaptor<Bundle> bundleCaptor;

    private FhirRepository repository;

    private static final String PERSON_ID = "personId";

    @BeforeEach
    void init() {
        this.repository = new HapiFhirRepository(fhirContextWrapper);
    }

    @ParameterizedTest
    @MethodSource("getAllBlankStrings")
    void whenBlankPersonIdGiven_throwException(String personId) {
        // Arrange
        HapiFhirRepository sut = new HapiFhirRepository(fhirContextWrapper);
        Practitioner practitioner = mock(Practitioner.class);

        // Act + Assert
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> sut.createPractitioner(practitioner, personId));
    }

    private static Stream<Arguments> getAllBlankStrings() {
        return Stream.of(
                Arguments.of((String) null),
                Arguments.of(""),
                Arguments.of("   "),
                Arguments.of("\t"),
                Arguments.of("\n"));
    }

    @Test
    void TestHapiRepository_WhenSavePractitioner_SendsBundleWithTransactionType() {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner()));
        var practitioner = new Practitioner();

        // Act
        repository.createPractitioner(practitioner, PERSON_ID);

        // Assert
        verify(fhirContextWrapper).executeTransaction(bundleCaptor.capture());
        var value = bundleCaptor.getValue();
        var type = value.getType();
        assertThat(type, equalTo(Bundle.BundleType.TRANSACTION));
    }

    @Test
    void TestHapiRepository_WhenSavePractitioner_ReturnsCorrectId() {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner()));
        var practitioner = new Practitioner();
        practitioner.setId("123");

        // Act
        var value = repository.createPractitioner(practitioner, PERSON_ID);

        // Assert
        assertThat(value, equalTo("123"));
    }

    @Test
    void TestHapiRepository_WhenContextWrapperThrowsExpected_ThrowsRestException() {
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenThrow(new ResourceNotFoundException("error"));
        var practitioner = new Practitioner();

        // Act + Assert
        assertThrows(RestException.class, () -> repository.createPractitioner(practitioner, PERSON_ID));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsMalformedBundle_ThrowsRestException() {
        // Arrange
        var responseBundle = new Bundle();
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(responseBundle);
        when(fhirContextWrapper.toListOfResources(any(Bundle.class))).thenReturn(List.of(new Practitioner(), new Practitioner()));
        var practitioner = new Practitioner();

        // Act + Assert
        assertThrows(RestException.class, () -> repository.createPractitioner(practitioner, PERSON_ID));
    }

    @Test
    void TestHapiRepository_WhenContextReturnsNull_ThrowsRestException() {
        // Arrange
        when(fhirContextWrapper.executeTransaction(any(Bundle.class))).thenReturn(null);
        var practitioner = new Practitioner();

        // Act + Assert
        assertThrows(RestException.class, () -> repository.createPractitioner(practitioner, PERSON_ID));
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
