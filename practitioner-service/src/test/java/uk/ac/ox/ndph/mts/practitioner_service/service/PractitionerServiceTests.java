package uk.ac.ox.ndph.mts.practitioner_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerUserAccount;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.model.ValidationResponse;
import uk.ac.ox.ndph.mts.practitioner_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.practitioner_service.validation.ModelEntityValidation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PractitionerServiceTests {

    @Captor
    ArgumentCaptor<Practitioner> practitionerCaptor;
    @Captor
    ArgumentCaptor<RoleAssignment> roleAssignmentCaptor;
    @Mock
    private EntityStore<Practitioner> practitionerStore;
    @Mock
    private ModelEntityValidation<Practitioner> practitionerValidator;
    @Mock
    private EntityStore<RoleAssignment> roleAssignmentStore;
    @Mock
    private ModelEntityValidation<RoleAssignment> roleAssignmentValidator;
    @Mock
    private ModelEntityValidation<PractitionerUserAccount> practitionerUserAccountValidator;

    private PractitionerService service;
    private PractitionerService serviceSpy;


    @BeforeEach
    void init() {
        this.service = new PractitionerService(practitionerStore, practitionerValidator,
                roleAssignmentStore, roleAssignmentValidator, practitionerUserAccountValidator);
        this.serviceSpy = Mockito.spy(service);
    }

    @Test
    void TestSavePractitioner_WithPractitioner_ValidatesPractitioner() {
        // Arrange
        Practitioner practitioner = new Practitioner(null, "prefix", "givenName", "familyName", "userAccountId");
        when(practitionerValidator.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(true, ""));
        when(practitionerStore.saveEntity(any(Practitioner.class))).thenReturn("123");

        //Act
        service.savePractitioner(practitioner);

        //Assert
        Mockito.verify(practitionerValidator).validate(practitionerCaptor.capture());
        var value = practitionerCaptor.getValue();
        assertEquals(practitioner, value);
    }

    @Test
    void TestSavePractitioner_WhenValidPractitioner_SavesToStore() {
        // Arrange
        Practitioner practitioner = new Practitioner(null, "prefix", "givenName", "familyName", "userAccountId");
        when(practitionerValidator.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(true, ""));
        when(practitionerStore.saveEntity(any(Practitioner.class))).thenReturn("123");
        //Act
        service.savePractitioner(practitioner);

        //Assert
        Mockito.verify(practitionerStore).saveEntity(practitionerCaptor.capture());
        var value = practitionerCaptor.getValue();
        assertEquals(practitioner, value);
    }

    @Test
    void TestSavePractitioner_WhenInvalidPractitioner_ThrowsValidationException() {
        // Arrange
        Practitioner practitioner = new Practitioner(null, "prefix", "givenName", "familyName", "userAccountId");
        when(practitionerValidator.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(false, "prefix"));
        //Act + Assert
        Assertions.assertThrows(ValidationException.class, () -> service.savePractitioner(practitioner),
                "Expecting save to throw validation exception");
    }

    @Test
    void TestSavePractitioner_WhenInvalidPractitioner_DoesntSavesToStore() {
        // Arrange
        Practitioner practitioner = new Practitioner(null, "prefix", "givenName", "familyName", "userAccountId");
        when(practitionerValidator.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(false, "prefix"));
        //Act + Assert
        Assertions.assertThrows(ValidationException.class, () -> service.savePractitioner(practitioner),
                "Expecting save to throw validation exception");
        Mockito.verify(practitionerStore, Mockito.times(0)).saveEntity(any(Practitioner.class));
    }

    @Test
    void TestPractitionerService_WhenNullInConstructor_ThrowsNullPointerException() {
        // Arrange + Act + Assert
        Assertions.assertThrows(NullPointerException.class, () -> new PractitionerService(null, practitionerValidator
                        , roleAssignmentStore, roleAssignmentValidator, practitionerUserAccountValidator),
                "null store should throw");
        Assertions.assertThrows(NullPointerException.class, () -> new PractitionerService(practitionerStore, null,
                        roleAssignmentStore, roleAssignmentValidator, practitionerUserAccountValidator),
                "null validation should throw");
        Assertions.assertThrows(NullPointerException.class, () -> new PractitionerService(practitionerStore,
                        practitionerValidator, null, roleAssignmentValidator, practitionerUserAccountValidator),
                "null store should throw");
        Assertions.assertThrows(NullPointerException.class, () -> new PractitionerService(practitionerStore, practitionerValidator,
                        roleAssignmentStore, null, practitionerUserAccountValidator),
                "null validation should throw");
        Assertions.assertThrows(NullPointerException.class, () -> new PractitionerService(practitionerStore, practitionerValidator,
                        roleAssignmentStore, roleAssignmentValidator, null),
                "null validation should throw");
    }

    @Test
    void TestGetPractitioner_CallsEntityStore() {
        String id = "42";
        Practitioner practitioner = new Practitioner(id, "prefix", "givenName", "familyName", "userAccountId");
        when(practitionerStore.getEntity(id)).thenReturn(java.util.Optional.of(practitioner));

        assertEquals(practitioner, service.findPractitionerById(id));
    }

    @Test
    void TestGetPractitioner_WhenStoreHasPractitioner_ReturnsPractitioner() {
        // arrange
        String id = "42";
        Practitioner practitioner = new Practitioner(id, "prefix", "givenName", "familyName", "userAccountId");
        when(practitionerStore.getEntity(id)).thenReturn(Optional.of(practitioner));
        // act
        final Practitioner returnedPractitioner = service.findPractitionerById(practitioner.getId());

        // assert
        assertTrue(new ReflectionEquals(practitioner).matches(returnedPractitioner));
    }

    @Test
    void TestGetPractitionerById_WhenStoreHasNoPractitioner_ThrowResponseStatusException() {
        // arrange
        when(practitionerStore.getEntity(anyString())).thenReturn(Optional.empty());
        // act and assert
        assertThrows(ResponseStatusException.class, () -> service.findPractitionerById("the-id"));
    }


    // RoleAssignment tests

    @Test
    void TestGetRoleAssignmentsByUserIdentity_WhenNonNullIdentity_ReturnListOfRoleAssignments() {
        // Arrange
        List<RoleAssignment> expectedResult = Collections.singletonList(
                new RoleAssignment("practitionerId", "siteId", "roleId"));
        when(service.getRoleAssignmentsByUserIdentity(anyString())).thenReturn(expectedResult);

        //Act
        List<RoleAssignment> result = service.getRoleAssignmentsByUserIdentity(anyString());

        //Assert
        assertEquals(expectedResult, result);
    }

    @Test
    void TestGetRoleAssignmentsByUserIdentity_WhenNullIdentity_ThrowsNullException() {
        //Act+Assert
        Assertions.assertThrows(NullPointerException.class, () -> service.getRoleAssignmentsByUserIdentity(null),
                "Null user identifier should throw.");
    }

    @Test
    void TestLinkParticipant_WhenValidEntity_ValidatesEntity() {
        // Arrange
        String practitionerId = "practitionerId";
        PractitionerUserAccount entity = new PractitionerUserAccount("practitionerId", "userAccountId");
        var foundPractitioner = new Practitioner(practitionerId, "prefix", "given", "family", "");
        doReturn(foundPractitioner).when(serviceSpy).findPractitionerById(practitionerId);
        when(practitionerUserAccountValidator.validate(any(PractitionerUserAccount.class))).thenReturn(new ValidationResponse(true, ""));
        when(practitionerStore.saveEntity(any(Practitioner.class))).thenReturn(practitionerId);

        //Act
        serviceSpy.linkPractitioner(entity);
        verify(practitionerUserAccountValidator).validate(entity);
    }
    
    @Test
    void TestSaveRoleAssignment_WhenValidEntity_ValidatesEntity() {
        // Arrange
        String practitionerId = "myPractitioner";
        RoleAssignment entity = new RoleAssignment(practitionerId, "siteId", "roleId");
        var foundPractitioner = new Practitioner(practitionerId, "2", "3", "4", "userAccountId");
        doReturn(foundPractitioner).when(serviceSpy).findPractitionerById(practitionerId);
        when(roleAssignmentValidator.validate(any(RoleAssignment.class))).thenReturn(new ValidationResponse(true, ""));
        when(roleAssignmentStore.saveEntity(any(RoleAssignment.class))).thenReturn("123");

        //Act
        serviceSpy.saveRoleAssignment(entity);

        //Assert
        Mockito.verify(roleAssignmentValidator).validate(roleAssignmentCaptor.capture());
        var entityFromValidator = roleAssignmentCaptor.getValue();
        assertEquals(entity, entityFromValidator);
    }

    @Test
    void TestSaveRoleAssignment_WhenValidEntity_SavesToStore() {
        // Arrange
        String practitionerId = "myPractitioner";

        var foundPractitioner = new Practitioner(practitionerId, "2", "3", "4", "userAccountId");
        doReturn(foundPractitioner).when(serviceSpy).findPractitionerById(practitionerId);

        RoleAssignment entity = new RoleAssignment(practitionerId, "siteId", "roleId");
        when(roleAssignmentValidator.validate(any(RoleAssignment.class))).thenReturn(new ValidationResponse(true, ""));
        when(roleAssignmentStore.saveEntity(any(RoleAssignment.class))).thenReturn("123");

        //Act
        serviceSpy.saveRoleAssignment(entity);

        //Assert
        Mockito.verify(roleAssignmentStore).saveEntity(roleAssignmentCaptor.capture());
        var entityFromValidator = roleAssignmentCaptor.getValue();
        assertEquals(entity, entityFromValidator);
    }

    @Test
    void TestSaveRoleAssignment_WhenPractitionerNotExist_ThrowsValidationException() {
        // Arrange
        String practitionerId = "unknown";
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(serviceSpy).findPractitionerById(practitionerId);

        RoleAssignment entity = new RoleAssignment(practitionerId, "siteId", "roleId");

        //Act + Assert
        Assertions.assertThrows(ValidationException.class, () -> serviceSpy.saveRoleAssignment(entity));
    }

    @Test
    void TestSaveRoleAssignment_WhenGetPractitionerReturnsOtherException_ThrowsThatException() {
        // Arrange
        var ex = new ResponseStatusException(HttpStatus.BAD_REQUEST);
        String practitionerId = "unknown";
        doThrow(ex).when(serviceSpy).findPractitionerById(practitionerId);

        RoleAssignment entity = new RoleAssignment(practitionerId, "siteId", "roleId");

        //Act + Assert
        Assertions.assertThrows(ex.getClass(), () -> serviceSpy.saveRoleAssignment(entity));
    }

    @Test
    void TestSaveRoleAssignment_WhenInvalidPractitioner_DoesntSaveToStore() {
        // Arrange
        RoleAssignment entity = new RoleAssignment(null, "site", "role");

        //Act + Assert
        var thrown = Assertions.assertThrows(ValidationException.class, () -> service.saveRoleAssignment(entity),
                "Expecting save to throw validation exception");
        Mockito.verify(roleAssignmentStore, Mockito.times(0)).saveEntity(any(RoleAssignment.class));
        assertThat(thrown.getMessage(), containsString("Practitioner"));
    }

    @Test
    void TestSaveRoleAssignment_WhenInvalidRole_DoesntSaveToStore() {
        // Arrange
        String practitionerId = "myPractitioner";
        RoleAssignment entity = new RoleAssignment(practitionerId, "site", null);

        var foundPractitioner = new Practitioner(practitionerId, "2", "3", "4", "userAccountId");
        doReturn(foundPractitioner).when(serviceSpy).findPractitionerById(practitionerId);
        when(roleAssignmentValidator.validate(any(RoleAssignment.class))).thenReturn(new ValidationResponse(false,
                "err"));

        //Act + Assert
        Assertions.assertThrows(ValidationException.class, () -> serviceSpy.saveRoleAssignment(entity),
                "Expecting save to throw validation exception");
        Mockito.verify(roleAssignmentStore, Mockito.times(0)).saveEntity(any(RoleAssignment.class));
    }

    @Test
    void TestGetRoleAssignmentsByUserIdentity_WithNullUserIdentity_ThrowsException() {

        Assertions.assertThrows(NullPointerException.class, () ->
                        service.getRoleAssignmentsByUserIdentity(null),
                "Identifier can not be null.");

        Mockito.verify(roleAssignmentStore,
                Mockito.times(0)).findEntitiesByUserIdentity(anyString());
    }

    @Test
    void TestGetRoleAssignmentsByUserIdentity_WithUserIdentity_ReturnsAssignmentRolesAsExpected() {

        String userIdentity = "userIdentity";
        List<RoleAssignment> expectedResult = Collections.singletonList(
                new RoleAssignment("practitionerId", "siteId", "roleId"));
        when(roleAssignmentStore.findEntitiesByUserIdentity(userIdentity)).thenReturn(expectedResult);

        List<RoleAssignment> actualResult = service.getRoleAssignmentsByUserIdentity(userIdentity);

        //Assert
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void TestGePractitionersByUserIdentity_WithUserIdentity_ReturnsPractitionersAsExpected() {

        String userID = "dummy-azureoid";
        List<Practitioner> reultFromStore = Collections.singletonList(
                new Practitioner("dummy-id", "dummy-prefix", "dummy-given-name", "dummy-family-name", userID));
        when(practitionerStore.findEntitiesByUserIdentity(userID)).thenReturn(reultFromStore);

        List<Practitioner> result = service.getPractitionersByUserIdentity(userID);

        assertEquals(userID, result.get(0).getUserAccountId());
    }
}
