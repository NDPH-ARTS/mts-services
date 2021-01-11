package uk.ac.ox.ndph.mts.site_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.service.EntityService;
import uk.ac.ox.ndph.mts.site_service.exception.RestException;
import uk.ac.ox.ndph.mts.site_service.exception.ValidationException;

@SpringBootTest(properties = { "server.error.include-message=always" })
@AutoConfigureMockMvc
public class SiteServiceAppTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntityService entityService;

    @Test
    void TestPostSite_WhenNoInput_Returns400() throws Exception {

        // Act + Assert
        this.mockMvc.perform(post("/site").contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    void TestPostSite_WhenValidInput_Returns201AndId() throws Exception {
        // Arrange
        when(entityService.saveSite(Mockito.any(Site.class))).thenReturn("123");
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\"}";
        // Act + Assert
        this.mockMvc
                .perform(post("/site").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostSite_WhenPartialInput_Returns201AndId() throws Exception {
        // Arrange
        when(entityService.saveSite(Mockito.any(Site.class))).thenReturn("123");
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\"}";
        // Act + Assert
        this.mockMvc
                .perform(post("/site").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostSite_WhenFhirDependencyFails_Returns502() throws Exception {
        // Arrange
        when(entityService.saveSite(Mockito.any(Site.class))).thenThrow(RestException.class);
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\"}";

        // Act + Assert
        this.mockMvc
                .perform(post("/site").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isBadGateway());
    }

    @Test
    void TestPostSite_WhenArgumentException_Returns400() throws Exception {
        // Arrange
        when(entityService.saveSite(Mockito.any(Site.class))).thenThrow(new ValidationException("prefix"));
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\"}";

        // Act + Assert
        String error = this.mockMvc
                .perform(post("/site").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isUnprocessableEntity()).andReturn().getResolvedException().getMessage();
        assertTrue(error.contains("prefix"));
    }
}
