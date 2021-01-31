package uk.ac.ox.ndph.mts.practitioner_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;

import java.util.Collections;

@SpringBootTest(properties = {"server.error.include-message=always"})
@AutoConfigureMockMvc
class PractitionerControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntityService entityService;

    private final String practitionerUri = "/practitioner";
    private final String roleAssignmentUri = "/practitioner/987/roles";
    private final String roleAssignmentByIdentifierUri = "/practitioner/roles";

    @Test
    void TestPostPractitioner_WhenNoBody_Returns400() throws Exception {
        // Act + Assert
        this.mockMvc.perform(post(practitionerUri).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    void TestPostPractitioner_WhenValidInput_Returns201AndId() throws Exception {
        // Arrange
        when(entityService.savePractitioner(Mockito.any(Practitioner.class))).thenReturn("123");
        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"givenName\", \"familyName\": \"familyName\"}";
        // Act + Assert
        this.mockMvc
                .perform(post(practitionerUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostPractitioner_WhenPartialInput_Returns201AndId() throws Exception {
        // Arrange
        when(entityService.savePractitioner(Mockito.any(Practitioner.class))).thenReturn("123");
        String jsonString = "{\"givenName\": \"givenName\", \"familyName\": \"familyName\"}";
        // Act + Assert
        this.mockMvc
                .perform(post(practitionerUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostPractitioner_WhenFhirDependencyFails_Returns502() throws Exception {
        // Arrange
        when(entityService.savePractitioner(Mockito.any(Practitioner.class))).thenThrow(RestException.class);
        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"givenName\", \"familyName\": \"familyName\"}";

        // Act + Assert
        this.mockMvc
                .perform(post(practitionerUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isBadGateway());
    }

    @Test
    void TestPostPractitioner_WhenArgumentException_Returns400() throws Exception {
        // Arrange
        when(entityService.savePractitioner(Mockito.any(Practitioner.class))).thenThrow(new ValidationException("prefix"));
        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"givenName\", \"familyName\": \"familyName\"}";

        // Act + Assert
        String error = this.mockMvc
                .perform(post(practitionerUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isUnprocessableEntity()).andReturn().getResolvedException().getMessage();
        assertThat(error, containsString("prefix"));
    }


    // RoleAssignment Tests

    @Test
    void TestPostRoleAssignment_WhenNoBody_Returns400() throws Exception {
        // Act + Assert
        this.mockMvc.perform(post(roleAssignmentUri).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn().getResolvedException();
    }

    @Test
    void TestPostRoleAssignment_WhenPartialInput_Returns201AndId() throws Exception {
        // We test that the controller doesn't do any logic
        // Arrange
        String returnedValue = "123";
        when(entityService.saveRoleAssignment(Mockito.any(RoleAssignment.class))).thenReturn(returnedValue);
        String jsonString = "{}";
        // Act + Assert
        this.mockMvc
                .perform(post(roleAssignmentUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString(returnedValue)));
    }

    @Test
    void TestPostRoleAssignment_WhenServiceFails_Returns502() throws Exception {
        // Arrange
        when(entityService.saveRoleAssignment(Mockito.any(RoleAssignment.class))).thenThrow(RestException.class);
        String jsonString = "{}";

        // Act + Assert
        this.mockMvc
                .perform(post(roleAssignmentUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print())
                .andExpect(status().isBadGateway());
    }

    @Test
    void TestGetRoleAssignmentByIdentifier_WithMissingIdentifierParam_Returns500() throws Exception {
        // We test that the endpoint requires identifier parameter
        // Arrange
        // Act + Assert
        String error = this.mockMvc
                .perform(get(roleAssignmentByIdentifierUri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError()).andReturn().getResolvedException().getMessage();

        assertThat(error, equalTo("A required parameter: identifier is blank or missing."));
    }

    @Test
    void TestGetRoleAssignmentByIdentifier_WithIdentifierParam_ReturnsRoleAssignmentAsExpected() throws Exception {

        // Arrange
        RoleAssignment expectedRoleAssignment = new RoleAssignment("practitionerId", "siteId", "roleId");
        when(entityService.getRoleAssignmentsByIdentifier("123")).thenReturn(Collections.singletonList(expectedRoleAssignment));

        String jsonExpectedRoleAssignment = "[{\"practitionerId\":\"practitionerId\",\"siteId\":\"siteId\",\"roleId\":\"roleId\"}]";

        // Act + Assert
        this.mockMvc
                .perform(get(roleAssignmentByIdentifierUri).param("identifier", "123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(
                        content().string(containsString(jsonExpectedRoleAssignment)));
    }

}
