package uk.ac.ox.ndph.mts.practitioner_service.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.practitioner_service.NullableConverter;
import uk.ac.ox.ndph.mts.practitioner_service.client.WebFluxRoleServiceClient;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleAssignmentValidationTests {

    @Mock
    private WebFluxRoleServiceClient roleServiceClient;

    @Captor
    ArgumentCaptor<String> roleIdCaptor;

    private RoleAssignmentValidation validator;

    @BeforeEach
    void setup() {
         this.validator = new RoleAssignmentValidation(roleServiceClient);
    }

    @ParameterizedTest
    @CsvSource({",,,practitionerId",
            ",siteId,roleId,practitionerId",
            "null,siteId,roleId,practitionerId",
            "practitionerId,,roleId,siteId",
            "practitionerId,null,roleId,siteId",
            "practitionerId,siteId,,roleId",
            "practitionerId,siteId,null,roleId",
    })
    void TestRoleAssignmentValidation_WhenFieldsAreEmptyOrNull_ThrowsValidationException(
            @ConvertWith(NullableConverter.class) String practitionerId, @ConvertWith(NullableConverter.class) String siteId,
            @ConvertWith(NullableConverter.class) String roleId, String expectedField) {
        // Arrange
        RoleAssignment roleAssignment = new RoleAssignment(practitionerId, siteId, roleId);
        // Act + Assert
        var result = validator.validate(roleAssignment);
        assertThat(result.isValid(), is(false));
        assertThat(result.getErrorMessage(), containsString(expectedField));
    }

    @Test
    void TestRoleAssignmentValidation_WhenValidRole_ReturnsValidResponse() {
        // Arrange
        final var roleId = "testRoleId";
        when(roleServiceClient.roleIdExists(roleId)).thenReturn(true);
        final var roleAssignment = new RoleAssignment("practitionerId", "siteId", roleId);
        // Act
        final var result = validator.validate(roleAssignment);
        // Assert
        assertThat(result.isValid(), is(true));
        Mockito.verify(roleServiceClient).roleIdExists(roleIdCaptor.capture());
        assertThat(roleId, equalTo(roleIdCaptor.getValue()));
    }

    @Test
    void TestRoleAssignmentValidation_WhenInvalidRole_ReturnsInvalidResponse() {
        // Arrange
        var roleId = "missingRoleId";
        when(roleServiceClient.roleIdExists(roleId)).thenReturn(false);
        final RoleAssignment roleAssignment = new RoleAssignment("practitionerId", "siteId", roleId);
        // Act
        var result = validator.validate(roleAssignment);
        // Assert
        assertThat(result.isValid(), is(false));
        assertThat(result.getErrorMessage(), containsString("roleId"));
        Mockito.verify(roleServiceClient).roleIdExists(roleIdCaptor.capture());
        var value = roleIdCaptor.getValue();
        assertThat(roleId, equalTo(value));
    }

    @Test
    void TestRoleAssignmentValidation_WhenServiceFails_ThrowsException() {
        // Arrange
        var roleId = "testRoleId";
        when(roleServiceClient.roleIdExists(roleId)).thenThrow(new RestException("mock"));
        final RoleAssignment roleAssignment = new RoleAssignment("practitionerId", "siteId", roleId);
        // Act
        // Assert
        assertThrows(RestException.class, () -> validator.validate(roleAssignment));
        Mockito.verify(roleServiceClient).roleIdExists(roleIdCaptor.capture());
        assertThat(roleId, equalTo(roleIdCaptor.getValue()));
    }

}
