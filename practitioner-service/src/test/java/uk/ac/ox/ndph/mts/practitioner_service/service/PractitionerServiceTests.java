package uk.ac.ox.ndph.mts.practitioner_service.service;

import org.junit.jupiter.api.Assertions;
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
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.practitioner_service.NullableConverter;
import uk.ac.ox.ndph.mts.practitioner_service.exception.BadRequestException;
import uk.ac.ox.ndph.mts.practitioner_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.model.ValidationResponse;
import uk.ac.ox.ndph.mts.practitioner_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.practitioner_service.validation.ModelEntityValidation;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PractitionerServiceTests {

    @Mock
    private EntityStore<Practitioner> practitionerStore;
    @Mock
    private ModelEntityValidation<Practitioner> practitionerValidator;
    @Captor
    ArgumentCaptor<Practitioner> practitionerCaptor;

    private static final String PRACTITIONER_ID = "practitionerId";
    private static final String USER_ACCOUNT_ID = "userAccountId";
    
    @Mock
    private EntityStore<RoleAssignment> roleAssignmentStore;
    @Mock
    private ModelEntityValidation<RoleAssignment> roleAssignmentValidator;
    @Captor
    ArgumentCaptor<RoleAssignment> roleAssignmentCaptor;
    
    private PractitionerService service;

    @BeforeEach
    void init() {
        this.service = new PractitionerService(practitionerStore, practitionerValidator,
                roleAssignmentStore, roleAssignmentValidator);
    }

    @Test
    void TestSavePractitioner_WithPractitioner_ValidatesPractitioner() {
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);
        when(practitionerValidator.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(true, ""));
        when(practitionerStore.saveEntity(any(Practitioner.class))).thenReturn("123");
        //Act
        service.savePractitioner(practitioner);

        //Assert
        Mockito.verify(practitionerValidator).validate(practitionerCaptor.capture());
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
        when(practitionerValidator.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(true, ""));
        when(practitionerStore.saveEntity(any(Practitioner.class))).thenReturn("123");
        //Act
        service.savePractitioner(practitioner);

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
        when(practitionerValidator.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(false, "prefix"));
        //Act + Assert
        Assertions.assertThrows(ValidationException.class, () -> service.savePractitioner(practitioner),
                "Expecting save to throw validation exception");
    }

    @Test
    void TestSavePractitioner_WhenInvalidPractitioner_DoesntSavesToStore() {
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);
        when(practitionerValidator.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(false, "prefix"));
        //Act + Assert
        Assertions.assertThrows(ValidationException.class, () -> service.savePractitioner(practitioner),
                "Expecting save to throw validation exception");
        Mockito.verify(practitionerStore, Mockito.times(0)).saveEntity(any(Practitioner.class));
    }

    @Test
    void TestPractitionerService_WhenNullValues_ThrowsInitialisationError() {
        // Arrange + Act + Assert
        Assertions.assertThrows(InitialisationError.class, () -> new PractitionerService(null, practitionerValidator
                                        , roleAssignmentStore, roleAssignmentValidator),
                                "null store should throw");
        Assertions.assertThrows(InitialisationError.class, () -> new PractitionerService(practitionerStore, null,
                                                                                         roleAssignmentStore, roleAssignmentValidator),
                                "null validation should throw");
        Assertions.assertThrows(InitialisationError.class, () -> new PractitionerService(practitionerStore,
                                                                                         practitionerValidator, null, roleAssignmentValidator),
                                "null store should throw");
        Assertions.assertThrows(InitialisationError.class, () -> new PractitionerService(practitionerStore, practitionerValidator,
                                                                                         roleAssignmentStore, null),
                                "null validation should throw");
    }

    @Test
    void TestGetPractitioner_CallsEntityStore() {
        String id = "42";
        Practitioner practitioner = new Practitioner(id, "pref", "given", "family");
        when(practitionerStore.getEntity(id)).thenReturn(java.util.Optional.of(practitioner));

        assertEquals(practitioner, service.findPractitionerById(id));
    }

    @Test
    void TestGetPractitioner_WhenStoreHasPractitioner_ReturnsPractitioner() {
        // arrange
        String id = "42";
        Practitioner practitioner = new Practitioner(id, "pref", "given", "family");
        when(practitionerStore.getEntity(id)).thenReturn(Optional.of(practitioner));
        // act
        final Practitioner returnedPractitioner = service.findPractitionerById(practitioner.getId());
        returnedPractitioner.setUserAccountId("aaa");
        // assert
        //TODO: this doesn't work
        assertTrue(new ReflectionEquals(practitioner).matches(returnedPractitioner));
    }

    @Test
    void TestGetPractitionerById_WhenStoreHasNoPractitioner_ThrowResponseStatusException() {
        // arrange
        when(practitionerStore.getEntity(anyString())).thenReturn(Optional.empty());
        // act and assert
        assertThrows(ResponseStatusException.class, () -> service.findPractitionerById("the-id"));
    }

//    @ParameterizedTest
//    @MethodSource("getBlankStrings")
//    void linkPractitioner_whenBlankUserIdProvided_throwClientError(
//            @ConvertWith(NullableConverter.class) String userAccountId) {
//        // Act + Assert
//        assertThatExceptionOfType(BadRequestException.class)
//                .isThrownBy(
//                        () -> practitionerService.linkPractitioner(userAccountId, PRACTITIONER_ID))
//                .withMessageContainingAll("User", "blank");
//    }
//
//    @ParameterizedTest
//    @MethodSource("getBlankStrings")
//    void linkPractitioner_whenBlankPractitionerIdProvided_throwClientError(
//            @ConvertWith(NullableConverter.class) String practitionerId) {
//        // Act + Assert
//        assertThatExceptionOfType(BadRequestException.class)
//                .isThrownBy(
//                        () -> practitionerService.linkPractitioner(USER_ACCOUNT_ID, practitionerId))
//                .withMessageContainingAll("Practitioner", "blank");
//    }
//
//    private static Stream<String> getBlankStrings() {
//        return Stream.of(null, "", "  ", "\t", "\n");
//    }
//
//    @Test
//    void linkPractitioner_whenValidPractitioner_thenSetUserAccountId() {
//        // Arrange
//        final Practitioner practitioner = new Practitioner("", "", "");
//        practitioner.setUserAccountId("");
//        when(practitionerStore.getEntity(anyString())).thenReturn(practitioner);
//
//        // Act
//        practitionerService.linkPractitioner(USER_ACCOUNT_ID, PRACTITIONER_ID);
//
//        // Assert
//        assertThat(practitioner.getUserAccountId(), equalTo(USER_ACCOUNT_ID));
//    }

    // RoleAssignment tests

    @Test
    void TestSaveRoleAssignment_WhenValidEntity_ValidatesEntity() {
        // Arrange
        RoleAssignment entity = new RoleAssignment("practitionerId", "siteId", "roleId");
        when(roleAssignmentValidator.validate(any(RoleAssignment.class))).thenReturn(new ValidationResponse(true, ""));
        when(roleAssignmentStore.saveEntity(any(RoleAssignment.class))).thenReturn("123");

        //Act
        service.saveRoleAssignment(entity);

        //Assert
        Mockito.verify(roleAssignmentValidator).validate(roleAssignmentCaptor.capture());
        var entityFromValidator = roleAssignmentCaptor.getValue();
        assertThat(entity, equalTo(entityFromValidator));
    }

    @Test
    void TestSaveRoleAssignment_WhenValidEntity_SavesToStore() {
        // Arrange
        RoleAssignment entity = new RoleAssignment("practitionerId", "siteId", "roleId");
        when(roleAssignmentValidator.validate(any(RoleAssignment.class))).thenReturn(new ValidationResponse(true, ""));
        when(roleAssignmentStore.saveEntity(any(RoleAssignment.class))).thenReturn("123");

        //Act
        service.saveRoleAssignment(entity);

        //Assert
        Mockito.verify(roleAssignmentStore).saveEntity(roleAssignmentCaptor.capture());
        var entityFromValidator = roleAssignmentCaptor.getValue();
        assertThat(entity, equalTo(entityFromValidator));
    }

    @Test
    void TestSaveRoleAssignment_WhenInvalidEntity_ThrowsValidationException() {
        // Arrange
        RoleAssignment entity = new RoleAssignment(null, null, null);
        when(roleAssignmentValidator.validate(any(RoleAssignment.class))).thenReturn(new ValidationResponse(false,
                "err"));

        //Act + Assert
        Assertions.assertThrows(ValidationException.class, () -> service.saveRoleAssignment(entity),
                "Expecting save to throw validation exception");
    }

    @Test
    void TestSaveRoleAssignment_WhenInvalidEntity_DoesntSavesToStore() {
        // Arrange
        RoleAssignment entity = new RoleAssignment(null, null, null);
        when(roleAssignmentValidator.validate(any(RoleAssignment.class))).thenReturn(new ValidationResponse(false,
                "err"));

        //Act + Assert
        Assertions.assertThrows(ValidationException.class, () -> service.saveRoleAssignment(entity),
                "Expecting save to throw validation exception");
        Mockito.verify(roleAssignmentStore, Mockito.times(0)).saveEntity(any(RoleAssignment.class));
    }
}
