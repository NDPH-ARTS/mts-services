package uk.ac.ox.ndph.mts.practitioner_service.validation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.practitioner_service.NullableConverter;
import uk.ac.ox.ndph.mts.practitioner_service.TestRolesServiceBackend;
import uk.ac.ox.ndph.mts.practitioner_service.client.WebFluxRoleServiceClient;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;

import java.net.HttpURLConnection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ContextConfiguration
class RoleAssignmentValidationTests {

    private TestRolesServiceBackend mockBackEnd;

    private RoleAssignmentValidation validator;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @BeforeEach void initEach() {
        this.mockBackEnd = TestRolesServiceBackend.autoStart();
        this.validator = new RoleAssignmentValidation(new WebFluxRoleServiceClient(webClientBuilder, mockBackEnd.getUrl()));
    }

    @AfterEach
    void cleanup() {
        this.mockBackEnd.shutdown();
    }

    @Test
    void testConfiguration() {
        assertThat(webClientBuilder, is(notNullValue()));
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
        mockBackEnd.queueRoleResponse(roleId);
        final var roleAssignment = new RoleAssignment("practitionerId", "siteId", roleId);
        // Act
        final var result = validator.validate(roleAssignment);
        // Assert
        assertThat(result.isValid(), is(true));
    }

    @Test
    void TestRoleAssignmentValidation_WhenInvalidRole_ReturnsInvalidResponse() {
        // Arrange
        var roleId = "missingRoleId";
        mockBackEnd.queueErrorResponse(HttpURLConnection.HTTP_NOT_FOUND);
        final RoleAssignment roleAssignment = new RoleAssignment("practitionerId", "siteId", roleId);
        // Act
        var result = validator.validate(roleAssignment);
        // Assert
        assertThat(result.isValid(), is(false));
        assertThat(result.getErrorMessage(), containsString("roleId"));
    }

    @Test
    void TestRoleAssignmentValidation_WhenServiceFails_ThrowsException() {
        // Arrange
        var roleId = "testRoleId";
        mockBackEnd.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        final RoleAssignment roleAssignment = new RoleAssignment("practitionerId", "siteId", roleId);
        // Act
        // Assert
        assertThrows(RestException.class, () -> validator.validate(roleAssignment));

    }

}
