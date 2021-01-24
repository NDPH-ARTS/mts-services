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
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;

@SpringBootTest(properties = { "spring.cloud.config.enabled=false", "server.error.include-message=always" })
@AutoConfigureMockMvc
class PractitionerControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntityService entityService;

    @Test
    void TestPostPractitioner_WhenNoBody_Returns400() throws Exception {
        // Act + Assert
        this.mockMvc.perform(post(getPractitionerUri()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    void TestPostPractitioner_WhenValidInput_Returns201AndId() throws Exception {
        // Arrange
        when(entityService.savePractitioner(Mockito.any(Practitioner.class))).thenReturn("123");
        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"givenName\", \"familyName\": \"familyName\"}";
        // Act + Assert
        this.mockMvc
                .perform(post("/practitioner").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostPractitioner_WhenPartialInput_Returns201AndId() throws Exception {
        // Arrange
        when(entityService.savePractitioner(Mockito.any(Practitioner.class))).thenReturn("123");
        String jsonString = "{\"givenName\": \"givenName\", \"familyName\": \"familyName\"}";
        // Act + Assert
        this.mockMvc
                .perform(post(getPractitionerUri()).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostPractitioner_WhenFhirDependencyFails_Returns502() throws Exception {
        // Arrange
        when(entityService.savePractitioner(Mockito.any(Practitioner.class))).thenThrow(RestException.class);
        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"givenName\", \"familyName\": \"familyName\"}";

        // Act + Assert
        this.mockMvc
                .perform(post(getPractitionerUri()).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isBadGateway());
    }

    @Test
    void TestPostPractitioner_WhenArgumentException_Returns400() throws Exception {
        // Arrange
        when(entityService.savePractitioner(Mockito.any(Practitioner.class))).thenThrow(new ValidationException("prefix"));
        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"givenName\", \"familyName\": \"familyName\"}";

        // Act + Assert
        String error = this.mockMvc
                .perform(post(getPractitionerUri()).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isUnprocessableEntity()).andReturn().getResolvedException().getMessage();
        assertThat(error, containsString("prefix"));
    }

    private String getPractitionerUri() {
        return MvcUriComponentsBuilder.fromMethodCall(on(PractitionerController.class)
                .savePractitioner(null)).build().toUriString();
    }


    // RoleAssignment Tests

    @Test
    void TestPostRoleAssignment_WhenNoBody_Returns400() throws Exception {
        // Act + Assert
        this.mockMvc.perform(post(getRoleAssignmentUri()).contentType(MediaType.APPLICATION_JSON))
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
                .perform(post(getRoleAssignmentUri()).contentType(MediaType.APPLICATION_JSON).content(jsonString))
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
                .perform(post(getRoleAssignmentUri()).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print())
                .andExpect(status().isBadGateway());
    }

    private String getRoleAssignmentUri() {
        return MvcUriComponentsBuilder.fromMethodCall(on(PractitionerController.class)
                .saveRoleAssignment("987", null)).build().toUriString();
    }

}
