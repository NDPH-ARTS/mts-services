package uk.ac.ox.ndph.mts.site_service;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ox.ndph.mts.site_service.exception.RestException;
import uk.ac.ox.ndph.mts.site_service.repository.FhirRepository;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"server.error.include-message=always", "spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles("test-all-required")
@AutoConfigureMockMvc
class SiteServiceImplIntegrationTests {

    private static final String SITES_ROUTE = "/sites";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    public FhirRepository repository;

    @Test
    void TestPostSite_WhenValidInput_Returns201AndId() throws Exception {
        // Arrange
        when(repository.findOrganizationByName(anyString())).thenReturn(null);
        when(repository.saveOrganization(any(Organization.class))).thenReturn("123");
        when(repository.saveResearchStudy(any(ResearchStudy.class))).thenReturn("789");

        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\"}";
        // Act + Assert
        this.mockMvc
                .perform(post(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostSite_WhenInvalidInput_ReturnsUnprocessableEntityAndDescription() throws Exception {
        // Arrange
        when(repository.saveOrganization(any(Organization.class))).thenReturn("123");

        String jsonString = "{\"name\": \"\", \"alias\": \"alias\"}";
        // Act + Assert
        var error = this.mockMvc
                .perform(post(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isUnprocessableEntity()).andReturn().getResolvedException().getMessage();
        assertThat(error, containsString("Name"));
    }

    @Test
    void TestPostSite_WhenValidInputAndRepositoryThrows_ReturnsBadGateway() throws Exception {
        // Arrange
        when(repository.findOrganizationByName(anyString())).thenReturn(null);
        when(repository.saveOrganization(any(Organization.class))).thenThrow(new RestException("test error"));

        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\", \"parentSiteId\": \"parentSiteId\"}";
        // Act + Assert
        var error = this.mockMvc
                .perform(post(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isBadGateway()).andReturn().getResolvedException().getMessage();
        assertThat(error, containsString("test error"));
    }

    @Test
    void TestPostSite_WhenValidParentInput_Returns201AndId() throws Exception {
        // Arrange
        when(repository.findOrganizationByName(anyString())).thenReturn(null);
        when(repository.saveOrganization(any(Organization.class))).thenReturn("123");
        when(repository.saveResearchStudy(any(ResearchStudy.class))).thenReturn("789");

        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\", \"parentSiteId\": \"parentSiteId\"}";
        // Act + Assert
        this.mockMvc
                .perform(post(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestGetSites_ReturnsList() throws Exception {
        // Arrange
        final var org = new Organization()
                .setName("CCO")
                .addAlias("Root");
        org.setId("this-is-my-id");
        when(repository.findOrganizations()).thenReturn(List.of(org));
        // Act + Assert
        this.mockMvc
                .perform(get(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string(containsString("\"this-is-my-id\"")));
    }

    @Test
    void TestGetSites_WhenNoSites_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(repository.findOrganizations()).thenReturn(Collections.emptyList());
        // Act + Assert
        final var message = this.mockMvc
                .perform(get(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotImplemented()).andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("No root site"));
    }

    @Test
    void TestGetSiteById_WhenSiteExists_ReturnsSite() throws Exception {
        // arrange
        // act
        // assert
    }

    @Test
    void TestGetSiteById_WhenRepositoryThrows_ReturnBadGateway() throws Exception {
        // arrange
        // act
        // assert
    }

    @Test
    void TestGetSiteById_WhenSiteDoesNotExist_ReturnsNotFoundError() throws Exception {
        // arrange
        // act
        // assert
    }

}
