package uk.ac.ox.ndph.mts.site_service.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.site_service.TestSiteConfiguration;
import uk.ac.ox.ndph.mts.site_service.exception.InvariantException;
import uk.ac.ox.ndph.mts.site_service.exception.RestException;
import uk.ac.ox.ndph.mts.site_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;
import uk.ac.ox.ndph.mts.site_service.service.SiteService;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"spring.cloud.config.discovery.enabled = false", "spring.cloud.config.enabled=false", "server.error.include-message=always", "spring.main.allow-bean-definition-overriding=true", "fhir.uri=http://localhost:8080"})
@AutoConfigureMockMvc
class SiteControllerTests {

    private static final String SITES_ROUTE = "/sites";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SiteService siteService;

    @TestConfiguration
    static class Config {
        private final TestSiteConfiguration configuration = new TestSiteConfiguration();

        @Primary
        @Bean
        public SiteConfiguration getSiteConfiguration() {
            return configuration.getSiteConfiguration();
        }
    }

    @Test
    void TestPostSite_WhenNoInput_Returns400() throws Exception {

        // Act + Assert
        this.mockMvc.perform(post(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    void TestPostSite_WhenValidInput_Returns201AndId() throws Exception {
        // Arrange
        when(siteService.save(Mockito.any(Site.class))).thenReturn("123");
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\"}";
        // Act + Assert
        this.mockMvc
                .perform(post(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostSite_WhenPartialInput_Returns201AndId() throws Exception {
        // Arrange
        when(siteService.save(Mockito.any(Site.class))).thenReturn("123");
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\"}";
        // Act + Assert
        this.mockMvc
                .perform(post(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostSite_WhenFhirDependencyFails_Returns502() throws Exception {
        // Arrange
        when(siteService.save(Mockito.any(Site.class))).thenThrow(RestException.class);
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\"}";

        // Act + Assert
        this.mockMvc
                .perform(post(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isBadGateway());
    }

    @Test
    void TestPostSite_WhenArgumentException_Returns400() throws Exception {
        // Arrange
        when(siteService.save(Mockito.any(Site.class))).thenThrow(new ValidationException("name"));
        final String jsonString = "{\"name\": \"name\", \"alias\": \"alias\"}";
        // Act + Assert
        final var error = this.mockMvc
                .perform(post(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isUnprocessableEntity()).andReturn().getResolvedException();
        assertThat(error, notNullValue());
        assertThat(error.getMessage(), containsString("name"));
    }


    @Test
    void TestGetSite_WhenNoSites_Returns501() throws Exception {
        // Arrange
        when(siteService.findSites()).thenThrow(new InvariantException("root"));
        // Act + Assert
        final var error = this.mockMvc
                .perform(get(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotImplemented()).andReturn().getResolvedException();
        assertThat(error, notNullValue());
        assertThat(error.getMessage(), containsString("root"));
    }

    @Test
    void TestGetSite_WhenSites_Returns200AndList() throws Exception {
        // arrange
        when(siteService.findSites()).thenReturn(Collections.singletonList(new Site("CCO", "Root")));
        // act
        final String result = this.mockMvc
                .perform(get(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        // assert - not perfect as need to parse to a JSON object to check keys/values properly
        assertThat(result, stringContainsInOrder("\"name\":", "\"CCO\""));
        assertThat(result, stringContainsInOrder("\"alias\":", "\"Root\""));
        assertThat(result, stringContainsInOrder("\"parentSiteId\":", "null"));
    }

    @Test
    void TestGetSite_WhenFhirDependencyFails_Returns502() throws Exception {
        // Arrange
        when(siteService.findSiteById(anyString())).thenThrow(RestException.class);
        // Act + Assert
        this.mockMvc
                .perform(get(SITES_ROUTE + "/id-that-does-not-exist")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadGateway());
    }


    @Test
    void TestGetSite_WhenIdFound_Returns200AndSite() throws Exception {
        // Arrange
        final String siteId = "the-site-id";
        when(siteService.findSiteById(siteId)).thenReturn(new Site(siteId, "TheSite", "the-alias", "parentId", "the-siteType"));
        // Act + Assert
        final String result = this.mockMvc
                .perform(get(SITES_ROUTE + "/" + siteId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(result, stringContainsInOrder("\"siteId\":", "\"" + siteId + "\""));
        assertThat(result, stringContainsInOrder("\"name\":", "\"TheSite\""));
        assertThat(result, stringContainsInOrder("\"alias\":", "\"the-alias\""));
        assertThat(result, stringContainsInOrder("\"parentSiteId\":", "\"parentId\""));
    }

    @Test
    void TestGetSite_WhenIdNotFound_Returns404() throws Exception {
        // Arrange
        final String siteId = "the-site-id";
        when(siteService.findSiteById(siteId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "site not found"));
        // Act + Assert
        this.mockMvc
                .perform(get(SITES_ROUTE + "/" + siteId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound());
    }

}
