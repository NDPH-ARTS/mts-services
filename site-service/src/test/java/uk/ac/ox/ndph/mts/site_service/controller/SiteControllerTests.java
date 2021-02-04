package uk.ac.ox.ndph.mts.site_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import uk.ac.ox.ndph.mts.site_service.exception.InvariantException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.service.SiteService;
import uk.ac.ox.ndph.mts.site_service.exception.RestException;
import uk.ac.ox.ndph.mts.site_service.exception.ValidationException;

import java.util.Collections;

@SpringBootTest(properties = { "server.error.include-message=always" })
@AutoConfigureMockMvc
class SiteControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SiteService siteService;

    @Test
    void TestPostSite_WhenNoInput_Returns400() throws Exception {

        // Act + Assert
        this.mockMvc.perform(post("/sites").contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    void TestPostSite_WhenValidInput_Returns201AndId() throws Exception {
        // Arrange
        when(siteService.save(Mockito.any(Site.class))).thenReturn("123");
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\"}";
        // Act + Assert
        this.mockMvc
                .perform(post("/sites").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostSite_WhenPartialInput_Returns201AndId() throws Exception {
        // Arrange
        when(siteService.save(Mockito.any(Site.class))).thenReturn("123");
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\"}";
        // Act + Assert
        this.mockMvc
                .perform(post("/sites").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostSite_WhenFhirDependencyFails_Returns502() throws Exception {
        // Arrange
        when(siteService.save(Mockito.any(Site.class))).thenThrow(RestException.class);
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\"}";

        // Act + Assert
        this.mockMvc
                .perform(post("/sites").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isBadGateway());
    }

    @Test
    void TestPostSite_WhenArgumentException_Returns400() throws Exception {
        // Arrange
        when(siteService.save(Mockito.any(Site.class))).thenThrow(new ValidationException("name"));
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\"}";

        // Act + Assert
        String error = this.mockMvc
                .perform(post("/sites").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isUnprocessableEntity()).andReturn().getResolvedException().getMessage();
        assertThat(error, containsString("name"));
    }


    @Test
    void TestGetSite_WhenNoSites_Returns501() throws Exception {
        // Arrange
        when(siteService.findSites()).thenThrow(new InvariantException("root"));
        // Act + Assert
        final String error = this.mockMvc
                .perform(get("/sites").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotImplemented()).andReturn().getResolvedException().getMessage();
        assertThat(error, containsString("root"));

    }

    @Test
    void TestGetSite_WhenSites_Returns200AndList() throws Exception {
        // arrange
        when(siteService.findSites()).thenReturn(Collections.singletonList(new Site("CCO", "Root")));
        // act
        final String result = this.mockMvc
                .perform(get("/sites").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        // assert - not perfect as need to parse to a JSON object to check keys/values properly
        assertThat(result, stringContainsInOrder("\"name\":", "\"CCO\""));
        assertThat(result, stringContainsInOrder("\"alias\":", "\"Root\""));
        assertThat(result, stringContainsInOrder("\"parentSiteId\":", "null"));
    }

    @Test
    void TestGetSite_WhenFhirDependencyFails_Returns502() throws Exception {
        // Arrange
        when(siteService.findSites()).thenThrow(RestException.class);
        // Act + Assert
        this.mockMvc
                .perform(get("/sites").contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadGateway());
    }

}
