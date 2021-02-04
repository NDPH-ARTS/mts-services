package uk.ac.ox.ndph.mts.site_service;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ox.ndph.mts.site_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.site_service.converter.SiteConverter;
import uk.ac.ox.ndph.mts.site_service.exception.RestException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.site_service.repository.FhirRepository;
import uk.ac.ox.ndph.mts.site_service.repository.FhirServerResponseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "server.error.include-message=always", "spring.main.allow-bean-definition-overriding=true" })
@ActiveProfiles("test-all-required")
@AutoConfigureMockMvc
class SiteServiceImplIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    public FhirRepository repository;

    @Mock
    EntityConverter<Organization, Site> fromOrgConverterMock;

    @Mock
    private EntityStore<Site> siteStore;

    @Test
    void TestPostSite_WhenValidInput_Returns201AndId() throws Exception {
        // Arrange
        when(repository.findOrganizationByName(anyString())).thenReturn(null);
        when(repository.saveOrganization(any(Organization.class))).thenReturn("123");
        when(repository.saveResearchStudy(any(ResearchStudy.class))).thenReturn("789");
        
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

    @Test
    void TestPostSite_WhenValidInputAndRepositoryThrows_ReturnsBadGateway() throws Exception {
        // Arrange
        when(repository.findOrganizationByName(anyString())).thenReturn(null);
        when(repository.saveOrganization(any(Organization.class))).thenThrow(new RestException("test error"));
        
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\", \"parentSiteId\": \"parentSiteId\"}";
        // Act + Assert
        var error = this.mockMvc
                .perform(post("/sites").contentType(MediaType.APPLICATION_JSON).content(jsonString))
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
                .perform(post("/sites").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @Test
    void TestHapiRepository_WhenSearchOrganizationByName_Success() {
        // Arrange
        final String alias = "the-alias";
        final String parentId = "the-parent-id";
        final String organizationName = "the-name";
        final Organization org = new Organization();
        org.setName(organizationName);
        org.setId("the-id");
        org.addAlias(alias);
        org.setPartOf(new Reference("Organization/" + parentId));
        final Site convertedSite = new SiteConverter().convert(org);

        // Arrange
        when(repository.findOrganizationByName(organizationName)).thenReturn(org);
        when(fromOrgConverterMock.convert(org)).thenReturn(convertedSite);

        //act
        Site site = siteStore.findOrganizationByName(organizationName);

        assertThat(site, null);
    }
}
