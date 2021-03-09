package uk.ac.ox.ndph.mts.site_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.site_service.exception.InvariantException;
import uk.ac.ox.ndph.mts.site_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.SiteAttributeConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;
import uk.ac.ox.ndph.mts.site_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.site_service.repository.TestSiteStore;
import uk.ac.ox.ndph.mts.site_service.validation.ModelEntityValidation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @Captor
    ArgumentCaptor<Site> siteCaptor;

    private static final List<SiteAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP = List.of(
            new SiteAttributeConfiguration("name", "string", "Name", "^[a-zA-Z]{1,35}$"),
            new SiteAttributeConfiguration("alias", "string","Alias", "^[a-zA-Z]{1,35}$"),
            new SiteAttributeConfiguration("parentSiteId", "string","Parent SiteId", ""),
            new SiteAttributeConfiguration("siteType", "string","Site Type", ""));

    private static final List<SiteAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP_CUSTOM = List.of(
            new SiteAttributeConfiguration("address", "address", "XAddress", ""));

    private static final List<SiteConfiguration> SITE_CONFIGURATION_LIST = List.of(
            new SiteConfiguration("Organization", "site", "REGION", ALL_REQUIRED_UNDER_35_MAP, null,
                    Collections.singletonList(new SiteConfiguration("Organization", "site", "COUNTRY", ALL_REQUIRED_UNDER_35_MAP, null,
                            Collections.singletonList(new SiteConfiguration("Organization", "site", "LCC", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, null)
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
                ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, SITE_CONFIGURATION_LIST);

        Site siteWithParent = new Site(name, alias, parent, type);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
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
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                        SITE_CONFIGURATION_LIST);

        Site site = new Site(name, alias, parent, siteType);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());

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
                "Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, null);

        Site site = new Site(name, alias);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
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
                "Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, null);

        Site site = new Site(name, alias);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(invalid("No Address in payload"));

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
                        SITE_CONFIGURATION_LIST);

        final var root = new Site("root-id", "Root", "root", null, "CCO");
        final var site = new Site(null, "name", "alias", root.getSiteId(), "REGION");
        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
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
                        SITE_CONFIGURATION_LIST);

        final var site = new Site(null, "name", "alias", null, "CCO");
        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
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
                "Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, null);

        final var site = new Site(null, "name", "alias", null, "root");
        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());

        when(siteStore.findRoot()).thenReturn(Optional.of(new Site()));
        //Act + Assert
        assertThrows(ValidationException.class, () -> siteService.save(site),
                "Root site already exists");
        Mockito.verify(siteStore, Mockito.times(0)).saveEntity(any(Site.class));
    }

    @Test
    void TestSiteServiceImpl_WhenNullValues_ThrowsInitialisationError() {
        // Arrange + Act + Assert
        assertThrows(NullPointerException.class, () -> new SiteServiceImpl(null, siteStore, siteValidation),
                "null configuration should throw");
        assertThrows(NullPointerException.class, () -> new SiteServiceImpl(new SiteConfiguration(), null, siteValidation),
                "null store should throw");
        assertThrows(NullPointerException.class, () -> new SiteServiceImpl(new SiteConfiguration(), siteStore, null),
                "null validation should throw");
    }

    @Test
    void TestGetSites_WhenEmpty_ThrowsInvariantException() {
        // arrange
        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, null);

        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
        when(siteStore.findAll()).thenReturn(Collections.emptyList());
        // act + assert
        assertThrows(InvariantException.class, siteService::findSites,
                "Expecting getSites to throw invariant exception");
    }

    @Test
    void TestGetSites_WhenStoreHasSites_ReturnsSites() {
        // arrange
        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, null);

        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
        final var site = new Site("CCO", "Root", null);
        when(siteStore.findAll()).thenReturn(Collections.singletonList(site));
        // act
        final List<Site> sites = siteService.findSites();
        // assert
        assertThat(sites, is(not(empty())));
        assertThat(sites.size(), equalTo(1));
        final Site found = sites.get(0);
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
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, null);

        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
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
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, null);

        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
        when(siteStore.findById(anyString())).thenReturn(Optional.empty());
        // act and assert
        assertThrows(ResponseStatusException.class, () -> siteService.findSiteById("the-id"));
    }

    @Test
    void TestFindRootSite_WhenStoreHasRoot_ReturnsRoot() {
        // arrange
        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, null);

        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
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
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, null);

        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
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
                        SITE_CONFIGURATION_LIST);

        Site site = new Site(name, alias, parent, type);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());

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
                        SITE_CONFIGURATION_LIST);

        Site site = new Site(name, alias, parent, type);
        var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
        when(siteStore.findById("parent")).thenReturn(Optional.of(new Site(name, alias, parent, parentType)));

        // act and assert
        assertThrows(ValidationException.class, () -> siteService.save(site), "Invalid Parent or type for trial site");
    }

    @Test
    void TestSaveSite_WhenValidSiteWithNoParentAndNoRoot_ThrowsInvalidSiteTypeForRootException() {
        // Arrange
        final var config =
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM,
                        SITE_CONFIGURATION_LIST);

        final var site = new Site(null, "name", "alias", null, "root");
        final var siteService = new SiteServiceImpl(config, siteStore, siteValidation);
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
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
            Collections.singletonList(new SiteConfiguration(
                "Organization", "site", "RCC", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, null
            )));
        when(siteValidation.validateCoreAttributes(any(Site.class))).thenReturn(ok());
        when(siteValidation.validateCustomAttributes(any(Site.class))).thenReturn(ok());
        final var testSiteStore = new TestSiteStore();
        //Act + Assert
        final var siteService = new SiteServiceImpl(config, testSiteStore, siteValidation);
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

}
