package uk.ac.ox.ndph.mts.site_service.service;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.practitionerserviceclient.PractitionerServiceClient;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;
import uk.ac.ox.ndph.mts.security.authorisation.AuthorisationService;
import uk.ac.ox.ndph.mts.site_service.exception.InvariantException;
import uk.ac.ox.ndph.mts.site_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.SiteAttributeConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.SiteDTO;
import uk.ac.ox.ndph.mts.site_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.site_service.repository.TestSiteStore;
import uk.ac.ox.ndph.mts.site_service.validation.ModelEntityValidation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.ac.ox.ndph.mts.site_service.model.ValidationResponse.invalid;
import static uk.ac.ox.ndph.mts.site_service.model.ValidationResponse.ok;

@ExtendWith(MockitoExtension.class)
class SiteServiceImplTests {

    @Mock
    private EntityStore<Site, String> siteStore;

    @Mock
    private ModelEntityValidation<Site> siteValidation;

    @Mock
    private AuthorisationService authService;

    @Mock
    private RoleServiceClient roleServClnt;

    @Mock
    private PractitionerServiceClient practServClnt;

    @Captor
    ArgumentCaptor<Site> siteCaptor;

    private static final List<SiteAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP = List.of(
            new SiteAttributeConfiguration("name", "string", "Name", "^[a-zA-Z]{1,35}$"),
            new SiteAttributeConfiguration("alias", "string","Alias", "^[a-zA-Z]{1,35}$"),
            new SiteAttributeConfiguration("parentSiteId", "string","Parent SiteId", ""),
            new SiteAttributeConfiguration("siteType", "string","Site Type", ""));

    private static final List<SiteAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP_CUSTOM = List.of(
            new SiteAttributeConfiguration("address", "address", "XAddress", ""));

    private static final List<SiteAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP_EXT = List.of(
            new SiteAttributeConfiguration("ext", "ext", "Extension", ""));


    private static final List<SiteConfiguration> SITE_CONFIGURATION_LIST = List.of(
            new SiteConfiguration("Organization", "site", "REGION", ALL_REQUIRED_UNDER_35_MAP, null, null,
                    Collections.singletonList(new SiteConfiguration("Organization", "site", "COUNTRY", ALL_REQUIRED_UNDER_35_MAP, null, null,
                            Collections.singletonList(new SiteConfiguration("Organization", "site", "LCC", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, ALL_REQUIRED_UNDER_35_MAP_EXT, null)
                            )))));

    @Test
    void TestSaveSiteParent_WithSiteParent_ValidatesSiteParent() {
        // Arrange
        String name = "name";
        String alias = "alias";
        String parent = "parent";
        String type = "REGION";
        String parentType = "CCO";
        // Arrange
        final var config = new SiteConfiguration("Organization", "site", "CCO",
                ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, ALL_REQUIRED_UNDER_35_MAP_EXT, SITE_CONFIGURATION_LIST);

        Site siteWithParent = new Site(name, alias, parent, type);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateExtAttributes(any(Site.class))).thenReturn(ok());
        when(siteStore.findById("parent")).thenReturn(Optional.of(new Site(name, alias, parent, parentType)));
        when(siteStore.saveEntity(any(Site.class))).thenReturn("123");

        //Act
        String result = siteService.save(siteWithParent);
        assertThat(result, equalTo("123"));

        //Assert
        Mockito.verify(siteValidation).validateCoreAttributes(siteCaptor.capture());
        var value = siteCaptor.getValue();
        assertThat(siteWithParent, equalTo(value));
    }

    @Test
    void TestSaveSite_WhenValidSite_SavesToStore() {
        // Arrange
        String name = "name";
        String alias = "alias";
        String parent = "parent";
        String siteType = "REGION";
        String parentType = "CCO";

        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, ALL_REQUIRED_UNDER_35_MAP_EXT,
                        SITE_CONFIGURATION_LIST);

        Site site = new Site(name, alias, parent, siteType);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateExtAttributes(any(Site.class))).thenReturn(ok());

        when(siteStore.findById("parent")).thenReturn(Optional.of(new Site(name, alias, parent, parentType)));
        when(siteStore.saveEntity(any(Site.class))).thenReturn("123");

        //Act
        siteService.save(site);

        //Assert
        Mockito.verify(siteStore).saveEntity(siteCaptor.capture());
        var value = siteCaptor.getValue();
        assertThat(site, equalTo(value));
    }

    @Test
    void TestSaveSite_WhenInvalidSite_ThrowsValidationException_DoesntSavesToStore() {
        // Arrange
        String name = "name";
        String alias = "alias";
        final var config = new SiteConfiguration(
                "Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, ALL_REQUIRED_UNDER_35_MAP_EXT, null);

        Site site = new Site(name, alias);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(invalid("name"));
        //Act + Assert
        assertThrows(ValidationException.class, () -> siteService.save(site),
                "Expecting save to throw validation exception");
        Mockito.verify(siteStore, Mockito.times(0)).saveEntity(any(Site.class));
    }

    @Test
    void TestSaveSite_WhenInvalidSiteCustomAttribute_ThrowsValidationException_DoesntSavesToStore() {
        // Arrange
        String name = "name";
        String alias = "alias";
        final var config = new SiteConfiguration(
                "Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, ALL_REQUIRED_UNDER_35_MAP_EXT, null);

        Site site = new Site(name, alias);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(invalid("No Address in payload"));
        when(siteValidation.validateExtAttributes(any(Site.class))).thenReturn(ok());

        //Act + Assert
        assertThrows(ValidationException.class, () -> siteService.save(site),
                "Expecting save to throw validation exception");
        Mockito.verify(siteStore, Mockito.times(0)).saveEntity(any(Site.class));
    }

    @Test
    void TestSaveSite_WhenValidSiteWithValidParent_SavesToStore() {
        // Arrange
        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                        ALL_REQUIRED_UNDER_35_MAP_EXT, SITE_CONFIGURATION_LIST);

        final var root = new Site("root-id", "Root", "root", null, "CCO");
        final var site = new Site(null, "name", "alias", root.getSiteId(), "REGION");
        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateExtAttributes(any(Site.class))).thenReturn(ok());
        when(siteStore.findById(root.getSiteId())).thenReturn(Optional.of(root));
        when(siteStore.saveEntity(any(Site.class))).thenReturn("123");
        //Act + Assert
        assertThat(siteService.save(site), equalTo("123"));
        Mockito.verify(siteStore, Mockito.times(1)).saveEntity(any(Site.class));
    }

    @Test
    void TestSaveSite_WhenValidSiteWithNoParentAndNoRoot_SavesToStore() {
        // Arrange
        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                        ALL_REQUIRED_UNDER_35_MAP_EXT, SITE_CONFIGURATION_LIST);

        final var site = new Site(null, "name", "alias", null, "CCO");
        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateExtAttributes(any(Site.class))).thenReturn(ok());
        when(siteStore.findRoot()).thenReturn(Optional.empty());
        when(siteStore.saveEntity(any(Site.class))).thenReturn("123");
        //Act + Assert
        assertThat(siteService.save(site), equalTo("123"));
        Mockito.verify(siteStore, Mockito.times(1)).saveEntity(any(Site.class));
    }

    @Test
    void TestSaveSite_WhenValidSiteWithNoParentAndHasRoot_ThrowsValidationException_DoesntSavesToStore() {
        // Arrange

        final var config = new SiteConfiguration(
                "Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                ALL_REQUIRED_UNDER_35_MAP_EXT, null);

        final var site = new Site(null, "name", "alias", null, "root");
        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateExtAttributes(any(Site.class))).thenReturn(ok());

        when(siteStore.findRoot()).thenReturn(Optional.of(new Site()));
        //Act + Assert
        assertThrows(ValidationException.class, () -> siteService.save(site),
                "Root site already exists");
        Mockito.verify(siteStore, Mockito.times(0)).saveEntity(any(Site.class));
    }

    @Test
    void TestSaveSite_WhenValidSiteWithEmptyParentAndHasRoot_ThrowsValidationException_DoesntSavesToStore() {
        // Arrange

        final var config = new SiteConfiguration(
            "Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
            ALL_REQUIRED_UNDER_35_MAP_EXT, null);

        final var site = new Site(null, "name", "alias", "", "root");
        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateExtAttributes(any(Site.class))).thenReturn(ok());

        when(siteStore.findRoot()).thenReturn(Optional.of(new Site()));
        //Act + Assert
        assertThrows(ValidationException.class, () -> siteService.save(site),
            "Root site already exists");
        Mockito.verify(siteStore, Mockito.times(0)).saveEntity(any(Site.class));
    }

    @Test
    void TestSiteServiceImpl_WhenNullValues_ThrowsInitialisationError() {
        // Arrange + Act + Assert
        assertThrows(NullPointerException.class, () -> new SiteServiceImpl(null, siteStore, siteValidation, null,
                authService, roleServClnt, practServClnt),
                "null configuration should throw");
        assertThrows(NullPointerException.class, () -> new SiteServiceImpl(new SiteConfiguration(), null, siteValidation,
                null, authService, roleServClnt, practServClnt),
                "null store should throw");
        assertThrows(NullPointerException.class, () -> new SiteServiceImpl(new SiteConfiguration(), siteStore, null,
                null, authService, roleServClnt, practServClnt),
                "null validation should throw");
    }

    @Test
    void TestGetSites_WhenEmpty_ThrowsInvariantException() {
        // arrange
        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                        ALL_REQUIRED_UNDER_35_MAP_EXT,null);

        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        when(siteStore.findAll()).thenReturn(Collections.emptyList());
        // act + assert
        assertThrows(InvariantException.class, siteService::findSites,
                "Expecting getSites to throw invariant exception");
    }

    @Test
    void TestGetSites_WhenStoreHasSites_ReturnsSites() {
        // arrange
        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                        ALL_REQUIRED_UNDER_35_MAP_EXT, null);

        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        final var site = new Site("CCO", "Root", null);
        when(siteStore.findAll()).thenReturn(Collections.singletonList(site));
        // act
        final List<SiteDTO> sites = siteService.findSites();
        // assert
        assertThat(sites, is(not(empty())));
        assertThat(sites.size(), equalTo(1));
        final SiteDTO found = sites.get(0);
        assertThat(found.getName(), equalTo(site.getName()));
        assertThat(found.getAlias(), equalTo(site.getAlias()));
        assertThat(found.getParentSiteId(), equalTo(site.getParentSiteId()));
        // was using assertThat(sites, hasItem(samePropertyValuesAs(site)));
        // but got weird NoSuchMethodError, possible classpath issue
    }

    @Test
    void TestFindSiteById_WhenStoreHasSite_ReturnsSite() {
        // arrange
        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                        ALL_REQUIRED_UNDER_35_MAP_EXT, null);

        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        final var site = new Site("my-site-id", "CCO", "Root", null, "CCO");
        when(siteStore.findById(site.getSiteId())).thenReturn(Optional.of(site));
        // act
        final Site siteFound = siteService.findSiteById(site.getSiteId());
        // assert
        assertThat(siteFound.getName(), equalTo(site.getName()));
        assertThat(siteFound.getAlias(), equalTo(site.getAlias()));
    }

    @Test
    void TestFindSiteById_WhenStoreHasNoSite_ThrowResponseStatusException() {
        // arrange
        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                        ALL_REQUIRED_UNDER_35_MAP_EXT,null);

        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        when(siteStore.findById(anyString())).thenReturn(Optional.empty());
        // act and assert
        assertThrows(ResponseStatusException.class, () -> siteService.findSiteById("the-id"));
    }

    @Test
    void TestFindRootSite_WhenStoreHasRoot_ReturnsRoot() {
        // arrange
        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                        ALL_REQUIRED_UNDER_35_MAP_EXT,null);

        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        final var root = new Site("my-root-id", "CCO", "Root", null, "CCO");
        when(siteStore.findRoot()).thenReturn(Optional.of(root));
        // act
        final Site siteFound = siteService.findRootSite();
        // assert
        assertThat(siteFound.getName(), equalTo(root.getName()));
        assertThat(siteFound.getAlias(), equalTo(root.getAlias()));
    }

    @Test
    void TestFindRootSite_WhenStoreHasNoRoot_ThrowsResponseStatusException() {
        // arrange
        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                        ALL_REQUIRED_UNDER_35_MAP_EXT,null);

        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        when(siteStore.findRoot()).thenReturn(Optional.empty());
        // act and assert
        assertThrows(ResponseStatusException.class, siteService::findRootSite);
    }

    @Test
    void TestFindParentSite_WhenStoreHasNoParent_ThrowsParentNotFoundException() {
        // Arrange
        String name = "name";
        String alias = "alias";
        String parent = "parent";
        String type = "REGION";

        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                        ALL_REQUIRED_UNDER_35_MAP_EXT, SITE_CONFIGURATION_LIST);

        Site site = new Site(name, alias, parent, type);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateExtAttributes(any(Site.class))).thenReturn(ok());

        when(siteStore.findById("parent")).thenReturn(Optional.empty());

        // act and assert
        assertThrows(ResponseStatusException.class, () -> siteService.save(site), "Site ID not found");
    }

    @Test
    void TestSaveSite_WhenInCorrectSiteType_ThrowsInvalidParentOrTypeException() {
        // Arrange
        String name = "name";
        String alias = "alias";
        String parent = "parent";
        String type = "LCC";
        String parentType = "CCO";

        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                        ALL_REQUIRED_UNDER_35_MAP_EXT, SITE_CONFIGURATION_LIST);

        Site site = new Site(name, alias, parent, type);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateExtAttributes(any(Site.class))).thenReturn(ok());
        when(siteStore.findById("parent")).thenReturn(Optional.of(new Site(name, alias, parent, parentType)));

        // act and assert
        assertThrows(ValidationException.class, () -> siteService.save(site), "Invalid Parent or type for trial site");
    }

    @Test
    void TestSaveSite_WhenValidSiteWithNoParentAndNoRoot_ThrowsInvalidSiteTypeForRootException() {
        // Arrange
        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                        ALL_REQUIRED_UNDER_35_MAP_EXT, SITE_CONFIGURATION_LIST);

        final var site = new Site(null, "name", "alias", null, "root");
        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation, null, authService, roleServClnt,
            practServClnt);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateExtAttributes(any(Site.class))).thenReturn(ok());
        when(siteStore.findRoot()).thenReturn(Optional.empty());

        // act and assert
        assertThrows(ValidationException.class, () -> siteService.save(site), "Invalid Site Type for Root");
    }

    @Test
    void TestSaveSite_WhenValidSiteWithDuplicateName_ThrowsValidationException_DoesNotSaveToStore() {
        // Arrange
        String name = "name";
        String alias = "alias";
        final var config = new SiteConfiguration(
            "Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                ALL_REQUIRED_UNDER_35_MAP_EXT,
            Collections.singletonList(new SiteConfiguration(
                "Organization", "site", "RCC", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                    ALL_REQUIRED_UNDER_35_MAP_EXT,null
            )));
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateExtAttributes(any(Site.class))).thenReturn(ok());
        final var testSiteStore = new TestSiteStore();
        //Act + Assert
        final var siteService = new SiteServiceImpl(config, testSiteStore, siteValidation, null, authService,
            roleServClnt, practServClnt);
        final String rootId = siteService.save(new Site("root", "root", null, "CCO"));
        final var site = new Site(name, alias, rootId, "RCC");
        final var site2 = new Site(name.toUpperCase(), alias + 'x', rootId, "RCC");
        assertThat(rootId, not(emptyOrNullString()));
        siteService.save(site);
        assertThrows(ValidationException.class, () -> siteService.save(site),
            "Expecting save to throw validation exception");
        assertThrows(ValidationException.class, () -> siteService.save(site2),
            "Expecting save to throw validation exception");
        assertThat(testSiteStore.findAll().size(), is(2));
    }

    @Test
    void TestFilterMySites_ForAdminUserWithRoleAssignment_ReturnsFilteredSitesOnly(){
        //Arrange
        final var config = new SiteConfiguration("Organization", "site", "CCO",
            ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, ALL_REQUIRED_UNDER_35_MAP_EXT,
            SITE_CONFIGURATION_LIST);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation, new SiteUtil(), authService,
            roleServClnt, practServClnt);
        var parentSite = new SiteDTO("cco", null);
        var childSite1 = new SiteDTO("regiona", parentSite.getSiteId());
        var grandChildSite1 = new SiteDTO("hospital", childSite1.getSiteId());
        var greatGrandChildSite1 = new SiteDTO("ward", grandChildSite1.getSiteId());
        var childSite2 = new SiteDTO("regionb", parentSite.getSiteId());
        final String permission = "view-site";

        List<SiteDTO> sitesToFilter = Lists
            .list(parentSite, childSite1, grandChildSite1, greatGrandChildSite1, childSite2);

        RoleAssignmentDTO suRoleAssignment1 = getRoleAssignment("superuser", parentSite.getSiteId());
        RoleAssignmentDTO adminRoleAssignment1 = getRoleAssignment("admin", childSite1.getSiteId());
        RoleAssignmentDTO adminRoleAssignment2 = getRoleAssignment("admin", grandChildSite1.getSiteId());
        RoleAssignmentDTO adminRoleAssignment3 = getRoleAssignment("admin", greatGrandChildSite1.getSiteId());

        RoleAssignmentDTO[] roleAssignments= {suRoleAssignment1, adminRoleAssignment1, adminRoleAssignment2, adminRoleAssignment3};

        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setId(permission);
        PermissionDTO[] permissions = {permissionDTO};

        RoleDTO roleDTO1 = new RoleDTO();
        roleDTO1.setPermissions(Arrays.asList(permissions));
        roleDTO1.setId("admin");

        RoleDTO[] roleDTOs = {roleDTO1};

        String userId = "123";
        String tokenString = "token";
        Consumer<HttpHeaders> token = PractitionerServiceClient.bearerAuth(tokenString);
        when(authService.getUserId()).thenReturn(userId);
        when(authService.getToken()).thenReturn(tokenString);

        when(practServClnt.getUserRoleAssignments(any(), any())).thenReturn(Arrays.asList(roleAssignments));
        when(roleServClnt.getPage(anyInt(), anyInt(), any(Consumer.class))).thenReturn(new PageImpl(Arrays.asList(roleDTOs)));
        //Act
        var authResponse = siteService.filterUserSites(sitesToFilter, "admin", permission);

        //Assert
        assertAll(
            () -> assertTrue(authResponse),
            () -> assertEquals(3, sitesToFilter.size()),
            () -> assertTrue(sitesToFilter.contains(childSite1)),
            () -> assertTrue(sitesToFilter.contains(grandChildSite1)),
            () -> assertTrue(sitesToFilter.contains(greatGrandChildSite1)),
            () -> assertFalse(sitesToFilter.contains(parentSite))
        );

    }

    @Test
    void TestFilterMySites_ForAdminUserWithRoleAssignmentButNoPerm_ReturnsFalse(){
        //Arrange
        final var config = new SiteConfiguration("Organization", "site", "CCO",
            ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, ALL_REQUIRED_UNDER_35_MAP_EXT,
            SITE_CONFIGURATION_LIST);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation, new SiteUtil(), authService,
            roleServClnt, practServClnt);
        var parentSite = new SiteDTO("cco", null);
        var childSite1 = new SiteDTO("regiona", parentSite.getSiteId());
        var grandChildSite1 = new SiteDTO("hospital", childSite1.getSiteId());
        var greatGrandChildSite1 = new SiteDTO("ward", grandChildSite1.getSiteId());
        var childSite2 = new SiteDTO("regionb", parentSite.getSiteId());
        final String permission = "view-site";

        List<SiteDTO> sitesToFilter = Lists
            .list(parentSite, childSite1, grandChildSite1, greatGrandChildSite1, childSite2);

        RoleAssignmentDTO suRoleAssignment1 = getRoleAssignment("superuser", parentSite.getSiteId());
        RoleAssignmentDTO adminRoleAssignment1 = getRoleAssignment("admin", childSite1.getSiteId());
        RoleAssignmentDTO adminRoleAssignment2 = getRoleAssignment("admin", grandChildSite1.getSiteId());
        RoleAssignmentDTO adminRoleAssignment3 = getRoleAssignment("admin", greatGrandChildSite1.getSiteId());

        RoleAssignmentDTO[] roleAssignments= {suRoleAssignment1, adminRoleAssignment1, adminRoleAssignment2, adminRoleAssignment3};

        RoleDTO roleDTO1 = new RoleDTO();
        roleDTO1.setPermissions(Arrays.asList());
        roleDTO1.setId("admin");

        RoleDTO[] roleDTOs = {roleDTO1};

        String userId = "123";
        String tokenString = "token";
        Consumer<HttpHeaders> token = PractitionerServiceClient.bearerAuth(tokenString);
        when(authService.getUserId()).thenReturn(userId);
        when(authService.getToken()).thenReturn(tokenString);

        when(practServClnt.getUserRoleAssignments(any(), any())).thenReturn(Arrays.asList(roleAssignments));
        when(roleServClnt.getPage(anyInt(), anyInt(), any(Consumer.class))).thenReturn(new PageImpl(Arrays.asList(roleDTOs)));
        //Act
        var authResponse = siteService.filterUserSites(sitesToFilter, "admin", permission);

        //Assert
        assertFalse(authResponse);

    }

    @Test
    void TestFilterMySites_ForNoRoleAssignments_ReturnsFalse(){
        //Arrange
        final var config = new SiteConfiguration("Organization", "site", "CCO",
            ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, ALL_REQUIRED_UNDER_35_MAP_EXT,
            SITE_CONFIGURATION_LIST);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation, new SiteUtil(), authService,
            roleServClnt, practServClnt);
        var parentSite = new SiteDTO("cco", null);
        var childSite1 = new SiteDTO("regiona", parentSite.getSiteId());
        var grandChildSite1 = new SiteDTO("hospital", childSite1.getSiteId());
        var greatGrandChildSite1 = new SiteDTO("ward", grandChildSite1.getSiteId());
        var childSite2 = new SiteDTO("regionb", parentSite.getSiteId());
        final String permission = "view-site";

        List<SiteDTO> sitesToFilter = Lists
            .list(parentSite, childSite1, grandChildSite1, greatGrandChildSite1, childSite2);

        when(practServClnt.getUserRoleAssignments(any(), any())).thenReturn(null);

        //Act
        var authResponse = siteService.filterUserSites(sitesToFilter, "admin", permission);

        //Assert
        assertFalse(authResponse);

    }

    @Test
    void TestFilterMySites_ForNoRoleWithRoleAssignments_ReturnsTrue(){
        //Arrange
        final var config = new SiteConfiguration("Organization", "site", "CCO",
            ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, ALL_REQUIRED_UNDER_35_MAP_EXT,
            SITE_CONFIGURATION_LIST);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation, new SiteUtil(), authService,
            roleServClnt, practServClnt);
        var parentSite = new SiteDTO("cco", null);
        var childSite1 = new SiteDTO("regiona", parentSite.getSiteId());
        var grandChildSite1 = new SiteDTO("hospital", childSite1.getSiteId());
        var greatGrandChildSite1 = new SiteDTO("ward", grandChildSite1.getSiteId());
        var childSite2 = new SiteDTO("regionb", parentSite.getSiteId());
        final String permission = "view-site";

        List<SiteDTO> sitesToFilter = Lists
            .list(parentSite, childSite1, grandChildSite1, greatGrandChildSite1, childSite2);

        RoleAssignmentDTO suRoleAssignment1 = getRoleAssignment("superuser", parentSite.getSiteId());
        RoleAssignmentDTO adminRoleAssignment1 = getRoleAssignment("admin", childSite1.getSiteId());
        RoleAssignmentDTO adminRoleAssignment2 = getRoleAssignment("admin", grandChildSite1.getSiteId());
        RoleAssignmentDTO adminRoleAssignment3 = getRoleAssignment("admin", greatGrandChildSite1.getSiteId());

        RoleAssignmentDTO[] roleAssignments= {suRoleAssignment1, adminRoleAssignment1, adminRoleAssignment2, adminRoleAssignment3};

        when(practServClnt.getUserRoleAssignments(any(), any())).thenReturn(Arrays.asList(roleAssignments));
        //Act
        var authResponse = siteService.filterUserSites(sitesToFilter, null, permission);

        //Assert
        assertTrue(authResponse);

    }

    @Test
    void TestFilterMySites_ForRoleNotInRoleAssignments_ReturnsFalse(){
        //Arrange
        final var config = new SiteConfiguration("Organization", "site", "CCO",
            ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, ALL_REQUIRED_UNDER_35_MAP_EXT,
            SITE_CONFIGURATION_LIST);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation, new SiteUtil(), authService,
            roleServClnt, practServClnt);
        var parentSite = new SiteDTO("cco", null);
        var childSite1 = new SiteDTO("regiona", parentSite.getSiteId());
        var grandChildSite1 = new SiteDTO("hospital", childSite1.getSiteId());
        var greatGrandChildSite1 = new SiteDTO("ward", grandChildSite1.getSiteId());
        var childSite2 = new SiteDTO("regionb", parentSite.getSiteId());
        final String permission = "view-site";

        List<SiteDTO> sitesToFilter = Lists
            .list(parentSite, childSite1, grandChildSite1, greatGrandChildSite1, childSite2);

        RoleAssignmentDTO suRoleAssignment1 = getRoleAssignment("superuser", parentSite.getSiteId());
        RoleAssignmentDTO adminRoleAssignment1 = getRoleAssignment("admin", childSite1.getSiteId());
        RoleAssignmentDTO adminRoleAssignment2 = getRoleAssignment("admin", grandChildSite1.getSiteId());
        RoleAssignmentDTO adminRoleAssignment3 = getRoleAssignment("admin", greatGrandChildSite1.getSiteId());

        RoleAssignmentDTO[] roleAssignments= {suRoleAssignment1, adminRoleAssignment1, adminRoleAssignment2, adminRoleAssignment3};

        when(practServClnt.getUserRoleAssignments(any(), any())).thenReturn(Arrays.asList(roleAssignments));
        //Act
        var authResponse = siteService.filterUserSites(sitesToFilter, "badRole", permission);

        //Assert
        assertFalse(authResponse);

    }

    @Test
    void TestFilterMySites_ThrowsException_ItReturnsFalse(){
        //Arrange
        final var config = new SiteConfiguration("Organization", "site", "CCO",
            ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, ALL_REQUIRED_UNDER_35_MAP_EXT,
            SITE_CONFIGURATION_LIST);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation, new SiteUtil(), authService,
            roleServClnt, practServClnt);
        var parentSite = new SiteDTO("cco", null);
        var childSite1 = new SiteDTO("regiona", parentSite.getSiteId());
        var grandChildSite1 = new SiteDTO("hospital", childSite1.getSiteId());
        var greatGrandChildSite1 = new SiteDTO("ward", grandChildSite1.getSiteId());
        var childSite2 = new SiteDTO("regionb", parentSite.getSiteId());
        final String permission = "view-site";

        List<SiteDTO> sitesToFilter = Lists
            .list(parentSite, childSite1, grandChildSite1, greatGrandChildSite1, childSite2);

        RoleAssignmentDTO suRoleAssignment1 = getRoleAssignment("superuser", parentSite.getSiteId());
        RoleAssignmentDTO adminRoleAssignment1 = getRoleAssignment("admin", childSite1.getSiteId());
        RoleAssignmentDTO adminRoleAssignment2 = getRoleAssignment("admin", grandChildSite1.getSiteId());
        RoleAssignmentDTO adminRoleAssignment3 = getRoleAssignment("admin", greatGrandChildSite1.getSiteId());

        RoleAssignmentDTO[] roleAssignments= {suRoleAssignment1, adminRoleAssignment1, adminRoleAssignment2, adminRoleAssignment3};

        when(practServClnt.getUserRoleAssignments(any(), any())).thenThrow(new RuntimeException());
        //Act
        boolean result = siteService.filterUserSites(sitesToFilter, "badRole", permission);
        //Assert
        assertFalse(result);

    }


    private RoleAssignmentDTO getRoleAssignment(String roleId, String siteId){
        RoleAssignmentDTO roleAssignmentDTO = new RoleAssignmentDTO();
        roleAssignmentDTO.setRoleId(roleId);
        roleAssignmentDTO.setSiteId(siteId);

        return roleAssignmentDTO;
    }

    @Test
    void TestFindParentSiteWithChildId_ReturnsParentIDs(){
        //Arrange
        final var config = new SiteConfiguration("Organization", "site", "CCO",
            ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, ALL_REQUIRED_UNDER_35_MAP_EXT,
            SITE_CONFIGURATION_LIST);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation, new SiteUtil(), authService,
            roleServClnt, practServClnt);

        var parentSite = new Site("cco", null);
        parentSite.setSiteId("1");
        parentSite.setParentSiteId(null);
        var childSite1 = new Site("regiona", parentSite.getSiteId());
        childSite1.setSiteId("2");
        childSite1.setParentSiteId(parentSite.getSiteId());
        var grandChildSite1 = new Site("hospital", childSite1.getSiteId());
        grandChildSite1.setSiteId("3");
        grandChildSite1.setParentSiteId(childSite1.getSiteId());

        List<Site> sites = Arrays.asList(parentSite, childSite1, grandChildSite1);

        //Act
        when(siteStore.findAll()).thenReturn(sites);
        List<String> result = siteService.findParentSiteIds(grandChildSite1.getSiteId());

        //Assert
        assertThat(result.size(), is(3));
        assertTrue(result.contains(childSite1.getSiteId()));
        assertTrue(result.contains(parentSite.getSiteId()));
    }

    @Test
    void TestFindSitesWithId_PopulatesParentNames(){
        //Arrange
        final var config = new SiteConfiguration("Organization", "site", "CCO",
            ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, ALL_REQUIRED_UNDER_35_MAP_EXT,
            SITE_CONFIGURATION_LIST);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation, new SiteUtil(), authService,
            roleServClnt, practServClnt);

        var parentSite = new Site("cco", null);
        parentSite.setSiteId("1");
        parentSite.setParentSiteId(null);
        parentSite.setName("parent");
        var childSite1 = new Site("regiona", parentSite.getSiteId());
        childSite1.setSiteId("2");
        childSite1.setParentSiteId(parentSite.getSiteId());
        childSite1.setName("child");
        var grandChildSite1 = new Site("hospital", childSite1.getSiteId());
        grandChildSite1.setSiteId("3");
        grandChildSite1.setParentSiteId(childSite1.getSiteId());
        grandChildSite1.setName("grandchild");

        List<Site> sites = Arrays.asList(parentSite, childSite1, grandChildSite1);

        //Act
        when(siteStore.findAll()).thenReturn(sites);
        List<SiteDTO> result = siteService.findSites();

        //Assert
        assertThat(result.size(), is(3));
        assertTrue(Objects.isNull(result.get(0).getParentSiteName()));
        assertTrue(result.get(1).getParentSiteName().equalsIgnoreCase("parent"));
        assertTrue(result.get(2).getParentSiteName().equalsIgnoreCase("child"));
    }


}
