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
import uk.ac.ox.ndph.mts.practitioner_service.client.WebFluxRoleServiceClient;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.repository.FhirRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "spring.cloud.config.enabled=false", "server.error.include-message=always",
        "spring.main.allow-bean-definition-overriding=true" })
@ActiveProfiles("test-all-required")
@AutoConfigureMockMvc
class PractitionerServiceIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    public FhirRepository repository;

    @MockBean
    private WebFluxRoleServiceClient roleServiceClient;

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
        when(roleServiceClient.roleIdExists("roleId")).thenReturn(true);

        String jsonString = "{\"siteId\": \"siteId\", \"roleId\": \"roleId\"}";
        // Act + Assert
        this.mockMvc
                .perform(post(roleAssignmentUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }
}
