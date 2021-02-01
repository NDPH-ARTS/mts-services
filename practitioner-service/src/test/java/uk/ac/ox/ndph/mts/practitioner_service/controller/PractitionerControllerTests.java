package uk.ac.ox.ndph.mts.practitioner_service.controller;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import uk.ac.ox.ndph.mts.practitioner_service.configuration.PractitionerConfigurationProvider;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerAttributeConfiguration;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerConfiguration;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;

@SpringBootTest(properties = { "spring.cloud.config.enabled=false", "server.error.include-message=always", "spring.main.allow-bean-definition-overriding=true" })
@ActiveProfiles("test-all-required")
@AutoConfigureMockMvc
class PractitionerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntityService entityService;

    private final String practitionerUri = "/practitioner";
    private final String roleAssignmentUri = "/practitioner/987/roles";

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
                .andExpect(status().isBadRequest());
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

}
