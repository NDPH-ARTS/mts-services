package uk.ac.ox.ndph.mts.practitioner_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.service.EntityService;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"server.error.include-message=always"})
@AutoConfigureMockMvc
class PractitionerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntityService entityService;

    private static final String PARAM_PRACTITIONER = "practitionerId";
    private static final String PARAM_USER = "userAccountId";

    @Test
    void TestPostPractitioner_WhenNoInput_Returns400() throws Exception {

        // Act + Assert
        this.mockMvc.perform(post("/practitioner").contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    void TestPostPractitioner_WhenValidInput_Returns201AndId() throws Exception {
        // Arrange
        when(entityService.savePractitioner(any(Practitioner.class))).thenReturn("123");
        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"givenName\", \"familyName\": \"familyName\"}";
        // Act + Assert
        this.mockMvc
                .perform(post("/practitioner").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostPractitioner_WhenPartialInput_Returns201AndId() throws Exception {
        // Arrange
        when(entityService.savePractitioner(any(Practitioner.class))).thenReturn("123");
        String jsonString = "{\"givenName\": \"givenName\", \"familyName\": \"familyName\"}";
        // Act + Assert
        this.mockMvc
                .perform(post("/practitioner").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostPractitioner_WhenFhirDependencyFails_Returns502() throws Exception {
        // Arrange
        when(entityService.savePractitioner(any(Practitioner.class))).thenThrow(RestException.class);
        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"givenName\", \"familyName\": \"familyName\"}";

        // Act + Assert
        this.mockMvc
                .perform(post("/practitioner").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isBadGateway());
    }

    @Test
    void TestPostPractitioner_WhenArgumentException_Returns400() throws Exception {
        // Arrange
        when(entityService.savePractitioner(any(Practitioner.class))).thenThrow(new ValidationException("prefix"));
        String jsonString = "{\"prefix\": \"prefix\", \"givenName\": \"givenName\", \"familyName\": \"familyName\"}";

        // Act + Assert
        String error = this.mockMvc
                .perform(post("/practitioner").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isUnprocessableEntity()).andReturn().getResolvedException().getMessage();
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
                        .contentType(MediaType.APPLICATION_JSON));
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

}
