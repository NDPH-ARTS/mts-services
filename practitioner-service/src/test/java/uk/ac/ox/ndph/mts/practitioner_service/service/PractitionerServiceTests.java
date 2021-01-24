package uk.ac.ox.ndph.mts.practitioner_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.practitioner_service.NullableConverter;
import uk.ac.ox.ndph.mts.practitioner_service.exception.BadRequestException;
import uk.ac.ox.ndph.mts.practitioner_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.ValidationResponse;
import uk.ac.ox.ndph.mts.practitioner_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.practitioner_service.validation.ModelEntityValidation;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PractitionerServiceTests {

    @Mock
    private EntityStore<Practitioner> practitionerStore;

    @Mock
    private ModelEntityValidation<Practitioner> practitionerValidation;

    @Captor
    ArgumentCaptor<Practitioner> practitionerCaptor;

    PractitionerService practitionerService;
    final String PRACTITIONER_ID = "practitionerId";
    final String USER_ACCOUNT_ID = "userAccountId";

    @BeforeEach
    public void setUp() {
        practitionerService = new PractitionerService(practitionerStore, practitionerValidation);
    }

    @Test
    void TestSavePractitioner_WithPractitioner_ValidatesPractitioner() {
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);
        when(practitionerValidation.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(true, ""));
        when(practitionerStore.saveEntity(any(Practitioner.class))).thenReturn("123");
        //Act
        practitionerService.savePractitioner(practitioner);

        //Assert
        Mockito.verify(practitionerValidation).validate(practitionerCaptor.capture());
        var value = practitionerCaptor.getValue();
        assertThat(practitioner, equalTo(value));
    }

    @Test
    void TestSavePractitioner_WhenValidPractitioner_SavesToStore() {
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);
        when(practitionerValidation.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(true, ""));
        when(practitionerStore.saveEntity(any(Practitioner.class))).thenReturn("123");
        //Act
        practitionerService.savePractitioner(practitioner);

        //Assert
        Mockito.verify(practitionerStore).saveEntity(practitionerCaptor.capture());
        var value = practitionerCaptor.getValue();
        assertThat(practitioner, equalTo(value));
    }

    @Test
    void TestSavePractitioner_WhenInvalidPractitioner_ThrowsValidationException() {
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);
        when(practitionerValidation.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(false, "prefix"));
        //Act + Assert
        assertThrows(ValidationException.class, () -> practitionerService.savePractitioner(practitioner),
                "Expecting save to throw validation exception");
    }

    @Test
    void TestSavePractitioner_WhenInvalidPractitioner_DoesntSavesToStore() {
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);
        when(practitionerValidation.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(false, "prefix"));
        //Act + Assert
        assertThrows(ValidationException.class, () -> practitionerService.savePractitioner(practitioner),
                "Expecting save to throw validation exception");
        Mockito.verify(practitionerStore, Mockito.times(0)).saveEntity(any(Practitioner.class));
    }

    @Test
    void TestPractitionerService_WhenNullValues_ThrowsInitialisationError() {
        // Arrange + Act + Assert
        assertThrows(InitialisationError.class, () -> new PractitionerService(null, practitionerValidation),
                "null store should throw");
        assertThrows(InitialisationError.class, () -> new PractitionerService(practitionerStore, null),
                "null validation should throw");
    }

    @ParameterizedTest
    @MethodSource("getBlankStrings")
    void linkPractitioner_whenBlankUserIdProvided_throwClientError(
            @ConvertWith(NullableConverter.class) String userAccountId) {
        // Act + Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(
                        () -> practitionerService.linkPractitioner(userAccountId, PRACTITIONER_ID))
                .withMessageContainingAll("User", "blank");
    }

    @ParameterizedTest
    @MethodSource("getBlankStrings")
    void linkPractitioner_whenBlankPractitionerIdProvided_throwClientError(
            @ConvertWith(NullableConverter.class) String practitionerId) {
        // Act + Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(
                        () -> practitionerService.linkPractitioner(USER_ACCOUNT_ID, practitionerId))
                .withMessageContainingAll("Practitioner", "blank");
    }

    private static Stream<String> getBlankStrings() {
        return Stream.of(null, "", "  ", "\t", "\n");
    }
}
