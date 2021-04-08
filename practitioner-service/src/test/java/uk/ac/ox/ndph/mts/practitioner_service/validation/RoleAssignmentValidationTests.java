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
import uk.ac.ox.ndph.mts.practitioner_service.client.SiteServiceClient;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
import uk.ac.ox.ndph.mts.security.authentication.SecurityContextUtil;

import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleAssignmentValidationTests {

    @Captor
    ArgumentCaptor<String> roleIdCaptor;
    @Captor
    ArgumentCaptor<String> siteIdCaptor;
    @Mock
    private RoleServiceClient roleServiceClient;
    @Mock
    private SiteServiceClient siteServiceClient;

    @Mock
    private SecurityContextUtil securityContextUtil;
    private RoleAssignmentValidation validator;

    @BeforeEach
    void setup() {
        this.validator = new RoleAssignmentValidation(roleServiceClient, siteServiceClient, securityContextUtil);
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
        when(roleServiceClient.entityIdExists(eq(roleId), any(Consumer.class))).thenReturn(true);
        when(siteServiceClient.entityIdExists(anyString())).thenReturn(true);

        final var roleAssignment = new RoleAssignment("practitionerId", "siteId", roleId);
        // Act
        final var result = validator.validate(roleAssignment);
        // Assert
        assertThat(result.isValid(), is(true));
        Mockito.verify(roleServiceClient).entityIdExists(roleIdCaptor.capture(), any(Consumer.class));
        assertThat(roleId, equalTo(roleIdCaptor.getValue()));
    }

    @Test
    void TestRoleAssignmentValidation_WhenInvalidRole_ReturnsInvalidResponse() {
        // Arrange
        var roleId = "missingRoleId";
        when(roleServiceClient.entityIdExists(eq(roleId), any(Consumer.class))).thenReturn(false);
        final RoleAssignment roleAssignment = new RoleAssignment("practitionerId", "siteId", roleId);
        // Act
        var result = validator.validate(roleAssignment);
        // Assert
        assertThat(result.isValid(), is(false));
        assertThat(result.getErrorMessage(), containsString("roleId"));
        Mockito.verify(roleServiceClient).entityIdExists(roleIdCaptor.capture(), any(Consumer.class));
        var value = roleIdCaptor.getValue();
        assertThat(roleId, equalTo(value));
    }

    @Test
    void TestRoleAssignmentValidation_WhenServiceFails_ThrowsException() {
        // Arrange
        var roleId = "testRoleId";
        when(roleServiceClient.entityIdExists(eq(roleId), any(Consumer.class))).thenThrow(new RestException("mock"));
        final RoleAssignment roleAssignment = new RoleAssignment("practitionerId", "siteId", roleId);
        // Act
        // Assert
        assertThrows(RestException.class, () -> validator.validate(roleAssignment));
        Mockito.verify(roleServiceClient).entityIdExists(roleIdCaptor.capture(), any(Consumer.class));
        assertThat(roleId, equalTo(roleIdCaptor.getValue()));
    }

    @Test
    void TestRoleAssignmentValidation_WhenValidSite_ReturnsValidResponse() {
        // Arrange
        final var siteId = "testSiteId";
        when(roleServiceClient.entityIdExists(anyString(), any(Consumer.class))).thenReturn(true);
        when(siteServiceClient.entityIdExists(siteId)).thenReturn(true);
        final var roleAssignment = new RoleAssignment("practitionerId", siteId, "roleId");
        // Act
        final var result = validator.validate(roleAssignment);
        // Assert
        assertThat(result.isValid(), is(true));
        Mockito.verify(siteServiceClient).entityIdExists(siteIdCaptor.capture());
        var value = siteIdCaptor.getValue();
        assertThat(siteId, equalTo(value));
    }

    @Test
    void TestRoleAssignmentValidation_WhenInvalidSite_ReturnsInvalidResponse() {
        // Arrange
        final var siteId = "missingSiteId";
        when(roleServiceClient.entityIdExists(anyString(), any(Consumer.class))).thenReturn(true);
        when(siteServiceClient.entityIdExists(siteId)).thenReturn(false);
        final var roleAssignment = new RoleAssignment("practitionerId", siteId, "roleId");
        // Act
        var result = validator.validate(roleAssignment);
        // Assert
        assertThat(result.isValid(), is(false));
        assertThat(result.getErrorMessage(), containsString("siteId"));
        Mockito.verify(siteServiceClient).entityIdExists(siteIdCaptor.capture());
        var value = siteIdCaptor.getValue();
        assertThat(siteId, equalTo(value));
    }

    @Test
    void TestRoleAssignmentValidation_WhenSiteServiceFails_ThrowsException() {
        // Arrange
        final var siteId = "testSiteId";
        when(roleServiceClient.entityIdExists(anyString(), any(Consumer.class))).thenReturn(true);
        when(siteServiceClient.entityIdExists(siteId)).thenThrow(new RestException("mock"));
        final RoleAssignment roleAssignment = new RoleAssignment("practitionerId", siteId, "roleId");
        // Act
        // Assert
        assertThrows(RestException.class, () -> validator.validate(roleAssignment));
        Mockito.verify(siteServiceClient).entityIdExists(siteIdCaptor.capture());
    }

}
