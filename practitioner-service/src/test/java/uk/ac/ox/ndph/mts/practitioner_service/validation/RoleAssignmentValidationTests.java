package uk.ac.ox.ndph.mts.practitioner_service.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.practitioner_service.NullableConverter;
import uk.ac.ox.ndph.mts.practitioner_service.client.WebFluxRoleServiceClient;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.model.PageableResult;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleDTO;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
class RoleAssignmentValidationTests {

    private MockWebServer mockBackEnd;

    private RoleAssignmentValidation validator;

    @BeforeEach void initEach() throws Exception {
        this.mockBackEnd = new MockWebServer();
        this.mockBackEnd.start();
        this.validator = new RoleAssignmentValidation(new WebFluxRoleServiceClient(WebClient.builder(), String.format("http://localhost:%s", mockBackEnd.getPort())));
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

    private void queueRoleResponse(final String roleId) throws Exception {
        final var roleObj = new RoleDTO();
        roleObj.setId(roleId);
        final var response =  PageableResult.singleton(roleObj);
        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));
    }

    @Test
    void TestRoleAssignmentValidation_WhenValidRole_ReturnsValidResponse() throws Exception {
        /*
        RoleDTO testRole = new RoleDTO();
        testRole.setId("test role");


        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(testRole))
                .addHeader("Content-Type", "application/json"));

        RoleDTO returnedRole = trialConfigService.sendToRoleService(testRole);

        assertNotNull(returnedRole);*/
        // Arrange
        final var roleId = "testRoleId";
        queueRoleResponse(roleId);
        final var roleAssignment = new RoleAssignment("practitionerId", "siteId", roleId);
        // Act
        final var result = validator.validate(roleAssignment);
        // Assert
        assertThat(result.isValid(), is(true));
    }

    @Test
    void TestRoleAssignmentValidation_WhenInvalidRole_ReturnsInvalidResponse() throws Exception {
        // Arrange
        var roleId = "missingRoleId";
        queueRoleResponse("not" + roleId);
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
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(500));
        final RoleAssignment roleAssignment = new RoleAssignment("practitionerId", "siteId", roleId);
        // Act
        // Assert
        assertThrows(RestException.class, () -> validator.validate(roleAssignment));

    }

}
