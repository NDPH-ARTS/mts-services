package uk.ac.ox.ndph.mts.site_service;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import uk.ac.ox.ndph.mts.site_service.exception.RestException;
import uk.ac.ox.ndph.mts.site_service.repository.FhirRepository;

@SpringBootTest(properties = { "server.error.include-message=always", "spring.main.allow-bean-definition-overriding=true" })
@ActiveProfiles("test-all-required")
@AutoConfigureMockMvc
class SiteServiceIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    public FhirRepository repository;

    @Test
    void TestPostSite_WhenValidInput_Returns201AndId() throws Exception {
        // Arrange
        when(repository.saveOrganization(any(Organization.class))).thenReturn("123");
        when(repository.saveResearchStudy(any(ResearchStudy.class))).thenReturn(any(ResearchStudy.class));
        
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\"}";
        // Act + Assert
        this.mockMvc
                .perform(post("/sites").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestPostSite_WhenInvalidInput_ReturnsUnprocessableEntityAndDescription() throws Exception {
        // Arrange
        when(repository.saveOrganization(any(Organization.class))).thenReturn("123");

        String jsonString = "{\"name\": \"\", \"alias\": \"alias\"}";
        // Act + Assert
        var error = this.mockMvc
                .perform(post("/sites").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isUnprocessableEntity()).andReturn().getResolvedException().getMessage();
        assertThat(error, containsString("Name"));
    }

    /*
    @Test
    void TestPostSite_WhenValidInputAndRepositoryThrows_ReturnsBadGateway() throws Exception {
        // Arrange
        when(repository.saveOrganization(any(Organization.class))).thenThrow(new RestException("test error"));
        
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\"}";
        // Act + Assert
        var error = this.mockMvc
                .perform(post("/sites").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isBadGateway()).andReturn().getResolvedException().getMessage();
        assertThat(error, containsString("test error"));
    }
    */

    @Test
    void TestPostSite_WhenValidParentInput_Returns201AndId() throws Exception {
        // Arrange
        when(repository.saveOrganization(any(Organization.class))).thenReturn("123");
        when(repository.saveResearchStudy(any(ResearchStudy.class))).thenReturn(any(ResearchStudy.class));

        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\", \"parentSiteId\": \"parentSiteId\"}";
        // Act + Assert
        this.mockMvc
                .perform(post("/sites").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }
}
