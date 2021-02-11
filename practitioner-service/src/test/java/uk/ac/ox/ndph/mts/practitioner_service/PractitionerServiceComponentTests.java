package uk.ac.ox.ndph.mts.practitioner_service;

import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ox.ndph.mts.practitioner_service.client.RoleServiceClient;
import uk.ac.ox.ndph.mts.practitioner_service.client.SiteServiceClient;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.repository.FhirRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This is a component like test for the practitioner-service.
 * It tests controller methods while mocking classes that communicate with external parties (fhir, other services).
 */
@SpringBootTest(properties = {"spring.cloud.config.discovery.enabled = false", "spring.cloud.config.enabled=false", "server.error.include-message=always",
        "spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles("test-all-required")
@AutoConfigureMockMvc
class PractitionerServiceComponentTests {

    @Autowired
    private MockMvc mockMvc;

    // Mock the components that do external calls outside this service.
    @MockBean
    public FhirRepository repository;
    @MockBean
    private RoleServiceClient roleServiceClient;
    @MockBean
    private SiteServiceClient siteServiceClient;

    private final String practitionerUri = "/practitioner";
    private final String roleAssignmentUri = "/practitioner/987/roles";

    @Test
    void TestPostPractitioner_WhenValidInput_Returns201AndId() throws Exception {
        // Arrange
        when(repository.savePractitioner(any(Practitioner.class))).thenReturn("123");

        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"givenName\", \"familyName\": \"familyName\"}";
        // Act + Assert
        this.mockMvc
                .perform(post(practitionerUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostPractitioner_WhenInvalidInput_ReturnsUnprocessableEntityAndDescription() throws Exception {
        // Arrange
        when(repository.savePractitioner(any(Practitioner.class))).thenReturn("123");

        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"\", \"familyName\": \"familyName\"}";
        // Act + Assert
        var error = this.mockMvc
                .perform(post(practitionerUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isUnprocessableEntity()).andReturn().getResolvedException().getMessage();
        assertThat(error, containsString("Given Name"));
    }

    @Test
    void TestPostPractitioner_WhenValidInputAndRepositoryThrows_ReturnsBadGateway() throws Exception {
        // Arrange
        when(repository.savePractitioner(any(Practitioner.class))).thenThrow(new RestException("test error"));

        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"givenName\", \"familyName\": \"familyName\"}";
        // Act + Assert
        var error = this.mockMvc
                .perform(post(practitionerUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isBadGateway()).andReturn().getResolvedException().getMessage();
        assertThat(error, containsString("test error"));
    }

    @Test
    void TestPostRoleAssignment_WhenValidInput_Returns201AndId() throws Exception {
        // Arrange
        when(repository.savePractitionerRole(any(PractitionerRole.class))).thenReturn("123");
        when(roleServiceClient.entityIdExists(anyString())).thenReturn(true);
        when(siteServiceClient.entityIdExists(anyString())).thenReturn(true);

        String jsonString = "{\"siteId\": \"siteId\", \"roleId\": \"roleId\"}";
        // Act + Assert
        this.mockMvc
                .perform(post(roleAssignmentUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostRoleAssignment_WhenInvalidInput_ReturnsUnprocessableEntityAndDescription() throws Exception {
        // Arrange
        when(repository.savePractitionerRole(any(PractitionerRole.class))).thenReturn("123");

        String jsonString = "{\"siteId\": \"\", \"roleId\": \"roleId\"}";
        // Act + Assert
        String errorMsg = this.mockMvc
                .perform(post(roleAssignmentUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isUnprocessableEntity()).andReturn().getResolvedException().getMessage();

        assertThat(errorMsg, containsString("siteId"));
    }

    @Test
    void TestPostRoleAssignment_WhenInvalidRole_ReturnsUnprocessableEntity() throws Exception {
        // Arrange
        when(repository.savePractitionerRole(any(PractitionerRole.class))).thenReturn("123");
        when(roleServiceClient.entityIdExists(anyString())).thenReturn(false);
        when(siteServiceClient.entityIdExists(anyString())).thenReturn(true);

        String jsonString = "{\"siteId\": \"siteId\", \"roleId\": \"roleId\"}";
        // Act + Assert
        String errorMsg = this.mockMvc
                .perform(post(roleAssignmentUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isUnprocessableEntity()).andReturn().getResolvedException().getMessage();

        assertThat(errorMsg, both(containsString("roleId")).and(containsString("exist")));
    }

    @Test
    void TestPostRoleAssignment_WhenInvalidSite_ReturnsUnprocessableEntity() throws Exception {
        // Arrange
        when(repository.savePractitionerRole(any(PractitionerRole.class))).thenReturn("123");
        when(roleServiceClient.entityIdExists(anyString())).thenReturn(true);
        when(siteServiceClient.entityIdExists(anyString())).thenReturn(false);

        String jsonString = "{\"siteId\": \"siteId\", \"roleId\": \"roleId\"}";
        // Act + Assert
        String errorMsg = this.mockMvc
                .perform(post(roleAssignmentUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isUnprocessableEntity()).andReturn().getResolvedException().getMessage();

        assertThat(errorMsg, both(containsString("siteId")).and(containsString("exist")));
    }

    @Test
    void TestPostRoleAssignment_WhenValidInputAndRepositoryThrows_ReturnsBadGateway() throws Exception {
        // Arrange
        when(repository.savePractitionerRole(any(PractitionerRole.class))).thenThrow(new RestException("test error"));
        when(roleServiceClient.entityIdExists(anyString())).thenReturn(true);
        when(siteServiceClient.entityIdExists(anyString())).thenReturn(true);

        String jsonString = "{\"siteId\": \"siteId\", \"roleId\": \"roleId\"}";
        // Act + Assert
        String errorMsg = this.mockMvc
                .perform(post(roleAssignmentUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isBadGateway()).andReturn().getResolvedException().getMessage();
        assertThat(errorMsg, containsString("test error"));
    }
}
