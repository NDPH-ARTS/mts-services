package uk.ac.ox.ndph.mts.site_service;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.site_service.exception.RestException;
import uk.ac.ox.ndph.mts.site_service.repository.FhirRepository;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"spring.cloud.config.discovery.enabled = false", "spring.cloud.config.enabled=false", "server.error.include-message=always", "spring.main.allow-bean-definition-overriding=true", "fhir.uri=http://localhost:8099", "role.service.uri=http://role-service:8082"})
@ActiveProfiles({"no-authZ"})
@AutoConfigureMockMvc
@Import(TestSiteConfiguration.class)
class SiteServiceComponentTests {

    private static final String SITES_ROUTE = "/sites";
    @MockBean
    public FhirRepository repository;


    @Autowired
    private MockMvc mockMvc;

    @WithMockUser
    @Test
    void TestPostSite_WhenValidInput_Returns201AndId() throws Exception {
        // Arrange
        final String rootSiteId = "root-site-id";
        final String parentSiteType = "CCO";
        final String siteType = "REGION";
        final Date lastUpdated = new Date(System.currentTimeMillis());
        final Organization root = new Organization();
        root.setId(rootSiteId);
        root.setImplicitRules(parentSiteType);
        root.getMeta().setLastUpdated(new Date(System.currentTimeMillis()));
        when(repository.findOrganizationById(rootSiteId)).thenReturn(Optional.of(root));
        when(repository.saveOrganization(any(Organization.class))).thenReturn("123");
        when(repository.saveResearchStudy(any(ResearchStudy.class))).thenReturn("789");
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\", \"parentSiteId\": \"" + rootSiteId + "\", \"siteType\": \"" + siteType + "\"}";
        // Act + Assert
        this.mockMvc
                .perform(post(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @WithMockUser
    @Test
    void TestPostSite_WhenInvalidInput_ReturnsUnprocessableEntityAndDescription() throws Exception {
        // Arrange
        when(repository.saveOrganization(any(Organization.class))).thenReturn("123");
        String jsonString = "{\"name\": \"\", \"alias\": \"alias\", \"parentSiteId\": \"parentSiteId\", \"siteType\": \"CCO\"}";
        // Act + Assert
        var error = this.mockMvc
                .perform(post(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isUnprocessableEntity()).andReturn().getResolvedException().getMessage();
        assertThat(error, containsString("Name"));
    }

    @WithMockUser
    @Test
    void TestPostSite_WhenValidInputAndRepositoryThrows_ReturnsBadGateway() throws Exception {
        // Arrange
        final String rootSiteId = "root-site-id";
        final String parentSiteType = "CCO";
        final Organization root = new Organization();
        root.setId(rootSiteId);
        root.setImplicitRules(parentSiteType);
        root.getMeta().setLastUpdated(new Date(System.currentTimeMillis()));
        when(repository.findOrganizationById(anyString())).thenReturn(Optional.of(root));
        when(repository.saveOrganization(any(Organization.class))).thenThrow(new RestException("test error"));
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\", \"parentSiteId\": \"parentSiteId\", \"siteType\": \"REGION\"}";
        // Act + Assert
        var error = this.mockMvc
                .perform(post(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isBadGateway()).andReturn().getResolvedException().getMessage();
        assertThat(error, containsString("test error"));
    }

    @WithMockUser
    @Test
    void TestPostSite_WhenValidParentInput_Returns201AndId() throws Exception {
        // Arrange
        final Organization root = new Organization();
        root.setId("parentSiteId");
        root.setImplicitRules("CCO");
        root.getMeta().setLastUpdated(new Date(System.currentTimeMillis()));
        when(repository.findOrganizationById(("parentSiteId"))).thenReturn(Optional.of(root));
        when(repository.saveOrganization(any(Organization.class))).thenReturn("123");
        when(repository.saveResearchStudy(any(ResearchStudy.class))).thenReturn("789");
        String jsonString = "{\"name\": \"name\", \"alias\": \"alias\", \"parentSiteId\": \"parentSiteId\", \"siteType\": \"REGION\"}";
        // Act + Assert
        this.mockMvc
                .perform(post(SITES_ROUTE).contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andDo(print()).andExpect(status().isCreated()).andExpect(content().string(containsString("123")));
    }

    @WithMockUser
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

    @WithMockUser
    @Test
    void TestGetSiteById_WhenSiteExists_ReturnsSite() throws Exception {
        // arrange
        final String id = "this-is-my-id";
        final var org = new Organization()
                .setName("CCO")
                .addAlias("Root");
        org.setId(id);
        org.getMeta().setLastUpdated(new Date(System.currentTimeMillis()));
        when(repository.findOrganizationById(org.getId())).thenReturn(Optional.of(org));
        // Act + Assert
        final var content = this.mockMvc
                .perform(get(SITES_ROUTE + "/" + org.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(content, matchesPattern("^\\{[^\\}]+\\}$"));
        assertThat(content, stringContainsInOrder("\"siteId\":", org.getId()));
        assertThat(content, stringContainsInOrder("\"name\":", org.getName()));
        assertThat(content, stringContainsInOrder("\"alias\":", org.getAlias().get(0).toString()));
    }

    @WithMockUser
    @Test
    void TestGetSiteById_WhenRepositoryThrows_ReturnBadGateway() throws Exception {
        // Arrange
        when(repository.findOrganizationById(anyString())).thenThrow(new RestException("test error"));
        final String id = "this-is-my-id";
        // Act + Assert
        var error = this.mockMvc
                .perform(get(SITES_ROUTE + "/" + id).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadGateway()).andReturn().getResolvedException().getMessage();
        assertThat(error, containsString("test error"));
    }

    @WithMockUser
    @Test
    void TestGetSiteById_WhenSiteDoesNotExist_ReturnsNotFoundError() throws Exception {
        // Arrange
        when(repository.findOrganizationById(anyString())).thenReturn(Optional.empty());
        final String id = "this-is-my-id";
        // Act + Assert
        var error = this.mockMvc
                .perform(get(SITES_ROUTE + "/" + id).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound()).andReturn().getResolvedException().getMessage();
        assertThat(error, containsString("not found"));
    }

    private RoleAssignmentDTO getRoleAssignment(String roleId, String siteId){
        RoleAssignmentDTO roleAssignmentDTO = new RoleAssignmentDTO();
        roleAssignmentDTO.setRoleId(roleId);
        roleAssignmentDTO.setSiteId(siteId);

        return roleAssignmentDTO;
    }

}
