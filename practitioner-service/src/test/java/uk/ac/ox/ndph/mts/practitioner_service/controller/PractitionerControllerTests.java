package uk.ac.ox.ndph.mts.practitioner_service.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;

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
    private static final String PARAM_PRACTITIONER = "practitionerId";
    private static final String PARAM_USER = "userAccountId";

    @Test
    void TestGetPractitioner_WithInvalidId_ReturnsPractitioner() throws Exception {
        // Arrange
        String practitionerId = "practitionerId";
        when(entityService.getPractitioner(practitionerId))
            .thenReturn(null);
        // Act + Assert
        this.mockMvc
                .perform(get(practitionerUri + "/" + practitionerId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void TestGetPractitioner_WithValidId() throws Exception {
        // Arrange
        String practitionerId = "practitionerId";
        when(entityService.getPractitioner(practitionerId))
            .thenReturn(new Practitioner("42", "prefix", "given", "family"));
        // Act + Assert
        this.mockMvc
                .perform(get(practitionerUri + "/" + practitionerId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("42"));
    }
    
    @Test
    void TestPostPractitioner_WhenNoBody_Returns400() throws Exception {
        // Act + Assert
        this.mockMvc.perform(post(practitionerUri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

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

    @Test
    void TestPostPractitioner_WhenArgumentException_Returns400() throws Exception {
        // Arrange
        when(entityService.savePractitioner(any(Practitioner.class))).thenThrow(new ValidationException("prefix"));
        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"givenName\", \"familyName\": \"familyName\"}";

        // Act + Assert
        Exception ex = this.mockMvc
                .perform(post(practitionerUri).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isUnprocessableEntity()).andReturn().getResolvedException();
        assert ex != null;
        String error = ex.getMessage();

        assertThat(error, containsString("prefix"));
    }

    @ParameterizedTest
    @MethodSource("atLeastOneParamNotPresent")
    void testLinkPractitioner_whenParamsNotPresent_error(String userParam, String practitionerParam) throws Exception {
        // Arrange
        final String USER_ACCOUNT = "userAccount";
        final String PRACTITIONER = "practitioner";

        // Act
        this.mockMvc.perform(post("/practitioner/link")
                        .param(userParam, USER_ACCOUNT)
                        .param(practitionerParam, PRACTITIONER)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
        // Assert
        verify(entityService, never()).linkPractitioner(USER_ACCOUNT, PRACTITIONER);
    }

    private static Stream<Arguments> atLeastOneParamNotPresent() {
        return Stream.of(
                Arguments.of("wrongUserParamName", PARAM_PRACTITIONER),
                Arguments.of(PARAM_USER, "wrongPractitionerParam"),
                Arguments.of("wrongUserParamName", "wrongPractitionerParam"));
    }

    @Test
    void testLinkPractitioner_whenParamsPresent_callsEntityService() throws Exception {
        // Arrange
        final String USER_ACCOUNT = "userAccount";
        final String PRACTITIONER = "practitioner";

        // Act
        this.mockMvc
                .perform(post("/practitioner/link")
                        .param(PARAM_USER, USER_ACCOUNT)
                        .param(PARAM_PRACTITIONER, PRACTITIONER)
                        .contentType(MediaType.APPLICATION_JSON));
        // Assert
        verify(entityService, times(1)).linkPractitioner(USER_ACCOUNT, PRACTITIONER);
    }


    // RoleAssignment Tests

    @Test
    void TestPostRoleAssignment_WhenNoBody_Returns400() throws Exception {
        // Act + Assert
        this.mockMvc.perform(post(roleAssignmentUri).contentType(MediaType.APPLICATION_JSON))
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
                .andExpect(status().isBadGateway());
    }

}
