package uk.ac.ox.ndph.mts.practitioner_service.controller;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerUserAccount;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;

import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ox.ndph.mts.security.authorisation.AuthorisationService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"spring.cloud.config.discovery.enabled = false", "spring.cloud.config.enabled=false", "server.error.include-message=always", "spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles({"no-authZ", "test-all-required"})
@AutoConfigureMockMvc
class PractitionerControllerTests {

    private final String practitionerUri = "/practitioner";
    private final String roleAssignmentUri = "/practitioner/987/roles";
    private final String roleAssignmentByUserIdentityUri = "/practitioner/roles";
    private final String practitionerLinkUri = "/practitioner/6/link";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EntityService entityService;

    @WithMockUser
    @Test
    void TestGetPractitioner_WithUnknownId_Returns404() throws Exception {
        // Arrange
        String practitionerId = "practitionerId";
        when(entityService.findPractitionerById(practitionerId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "site not found"));
        // Act + Assert
        this.mockMvc
                .perform(get(practitionerUri + "/" + practitionerId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    void TestGetPractitioner_WithValidId_Returns200AndPractitioner() throws Exception {
        // Arrange
        String practitionerId = "practitionerId";
        when(entityService.findPractitionerById(practitionerId))
                .thenReturn(new Practitioner("42", "prefix", "given", "family", "userAccountId"));
        // Act + Assert
        this.mockMvc
                .perform(get(practitionerUri + "/" + practitionerId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("42"));
    }

    @WithMockUser
    @Test
    void TestPostPractitioner_WhenNoBody_Returns400() throws Exception {
        // Act + Assert
        this.mockMvc.perform(post(practitionerUri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    void TestPostPractitioner_WhenValidInput_Returns201AndId() throws Exception {
        // Arrange
        when(entityService.savePractitioner(any(Practitioner.class))).thenReturn("123");
        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"givenName\", \"familyName\": \"familyName\"}";
        // Act + Assert
        this.mockMvc
                .perform(post(practitionerUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @WithMockUser
    @Test
    void TestPostPractitioner_WhenPartialInput_Returns201AndId() throws Exception {
        // Arrange
        when(entityService.savePractitioner(any(Practitioner.class))).thenReturn("123");
        String jsonString = "{\"givenName\": \"givenName\", \"familyName\": \"familyName\"}";
        // Act + Assert
        this.mockMvc
                .perform(post(practitionerUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @WithMockUser
    @Test
    void TestPostPractitioner_WhenFhirDependencyFails_Returns502() throws Exception {
        // Arrange
        when(entityService.savePractitioner(any(Practitioner.class))).thenThrow(RestException.class);
        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"givenName\", \"familyName\": \"familyName\"}";

        // Act + Assert
        this.mockMvc
                .perform(post(practitionerUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isBadGateway());
    }

    @WithMockUser
    @Test
    void TestPostPractitioner_WhenArgumentException_Returns422() throws Exception {
        // Arrange
        when(entityService.savePractitioner(any(Practitioner.class))).thenThrow(new ValidationException("prefix"));
        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"givenName\", \"familyName\": \"familyName\"}";

        // Act + Assert
        String error = this.mockMvc
                .perform(post(practitionerUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isUnprocessableEntity()).andReturn().getResolvedException().getMessage();
        assertThat(error, containsString("prefix"));
    }

    @WithMockUser
    @Test
    void TestLinkPractitioner_whenParamsNotPresent_error() throws Exception {
        // Act
        this.mockMvc.perform(post(practitionerLinkUri)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
        // Assert
        verify(entityService, never()).linkPractitioner((any(PractitionerUserAccount.class)));
    }

    @WithMockUser
    @Test
    void linkPractitioner_whenLinkSucceeds_thenReturnCreatedStatus() throws Exception {
        // Arrange
        doNothing().when(entityService).linkPractitioner(any(PractitionerUserAccount.class));

        // Act + Assert
        this.mockMvc
                .perform(post(practitionerLinkUri)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(new ObjectMapper().writeValueAsString(new PractitionerUserAccount("", "directoryId"))))
                                 .andExpect(status().isOk());
    }

    // RoleAssignment Tests

    @WithMockUser
    @Test
    void TestPostRoleAssignment_WhenNoBody_Returns400() throws Exception {
        // Act + Assert
        this.mockMvc.perform(post(roleAssignmentUri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    void TestPostRoleAssignment_WhenPartialInput_Returns201AndId() throws Exception {
        // We test that the controller doesn't do any logic
        // Arrange
        String returnedValue = "123";
        when(entityService.saveRoleAssignment(any(RoleAssignment.class))).thenReturn(returnedValue);

        String jsonString = "{\"siteId\": \"abc\"}";
        // Act + Assert
        this.mockMvc
                .perform(post(roleAssignmentUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString(returnedValue)));
    }
    @WithMockUser
    @Test
    void TestPostRoleAssignment_WhenServiceFails_Returns502() throws Exception {
        // Arrange
        when(entityService.saveRoleAssignment(any(RoleAssignment.class))).thenThrow(RestException.class);
        String jsonString = "{\"siteId\": \"abc\", \"roleId\": \"123\"}";

        // Act + Assert
        this.mockMvc
                .perform(post(roleAssignmentUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isBadGateway());
    }

    @WithMockUser
    @Test
    void TestGetRoleAssignmentByUserIdentity_WithMissingUserIdentityParam_Returns500() throws Exception {
        // We test that the endpoint requires user identity parameter
        // Arrange
        // Act + Assert
        String error = this.mockMvc
                .perform(get(roleAssignmentByUserIdentityUri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError()).andReturn().getResolvedException().getMessage();

        assertThat(error, equalTo("Required String parameter 'userIdentity' is not present"));
    }

    @WithMockUser
    @Test
    void TestGetRoleAssignmentByUserIdentity_WithNullUserIdentityParam_Returns500() throws Exception {
        // We test that the endpoint requires user identity parameter
        // Arrange
        // Act + Assert
        String error = this.mockMvc
                .perform(get(roleAssignmentByUserIdentityUri).param("userIdentity", Strings.EMPTY)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError()).andReturn().getResolvedException().getMessage();

        assertThat(error, equalTo("Required String parameter 'userIdentity' is blank"));
    }

    @WithMockUser
    @Test
    void TestGetRoleAssignmentByUserIdentity_WithUserIdentityParam_ReturnsRoleAssignmentAsExpected() throws Exception {

        // Arrange
        RoleAssignment expectedRoleAssignment = new RoleAssignment("practitionerId", "siteId", "roleId");
        when(entityService.getRoleAssignmentsByUserIdentity("123")).thenReturn(Collections.singletonList(expectedRoleAssignment));

        String jsonExpectedRoleAssignment = "[{\"practitionerId\":\"practitionerId\",\"siteId\":\"siteId\",\"roleId\":\"roleId\"}]";

        // Act + Assert
        this.mockMvc
                .perform(get(roleAssignmentByUserIdentityUri).param("userIdentity", "123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(
                        content().string(containsString(jsonExpectedRoleAssignment)));
    }

}
