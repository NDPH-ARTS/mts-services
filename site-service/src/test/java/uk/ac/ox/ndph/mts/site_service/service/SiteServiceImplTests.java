package uk.ac.ox.ndph.mts.site_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.site_service.configuration.SiteConfigurationProvider;
import uk.ac.ox.ndph.mts.site_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.site_service.exception.InvariantException;
import uk.ac.ox.ndph.mts.site_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.SiteAttributeConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.ValidationResponse;
import uk.ac.ox.ndph.mts.site_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.site_service.validation.ModelEntityValidation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SiteServiceImplTests {

    @Mock
    private SiteConfigurationProvider configurationProvider;

    @Mock
    private EntityStore<Site, String> siteStore;

    @Mock
    private ModelEntityValidation<Site> siteValidation;

    @Captor
    ArgumentCaptor<Site> siteCaptor;

    private static final List<SiteAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP = List.of(
            new SiteAttributeConfiguration("name", "Name", "^[a-zA-Z]{1,35}$"),
            new SiteAttributeConfiguration("alias", "Alias", "^[a-zA-Z]{1,35}$"),
            new SiteAttributeConfiguration("parentSiteId", "Parent SiteId", ""),
            new SiteAttributeConfiguration("siteType", "Site Type", ""));

    private static final List<SiteConfiguration> SITE_CONFIGURATION_LIST  = List.of(
            new SiteConfiguration("Organization", "site", "REGION", ALL_REQUIRED_UNDER_35_MAP,
                    Collections.singletonList(new SiteConfiguration("Organization", "site", "COUNTRY", ALL_REQUIRED_UNDER_35_MAP,
                        Collections.singletonList(new SiteConfiguration("Organization", "site", "LCC", ALL_REQUIRED_UNDER_35_MAP, null)
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
        when(configurationProvider.getConfiguration()).thenReturn(
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP,
                        SITE_CONFIGURATION_LIST));

        Site siteWithParent = new Site(name, alias, parent, type);
        var siteService = new SiteServiceImpl(configurationProvider, siteStore, siteValidation);
        when(siteValidation.validate(any(Site.class))).thenReturn(new ValidationResponse(true, ""));
        when(siteStore.findById("parent")).thenReturn(Optional.of(new Site(name, alias, parent, parentType)));
        when(siteStore.saveEntity(any(Site.class))).thenReturn("123");

        //Act
        String result = siteService.save(siteWithParent);
        assertThat(result, equalTo("123"));

        //Assert
        Mockito.verify(siteValidation).validate(siteCaptor.capture());
        var value = siteCaptor.getValue();
        assertThat(siteWithParent, equalTo(value));
    }

    @Test
    void TestSaveSite_WhenValidSite_SavesToStore() {
        // Arrange
        String name = "name";
        String alias = "alias";
        when(configurationProvider.getConfiguration()).thenReturn(
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, null));

        Site site = new Site(name, alias);
        var siteService = new SiteServiceImpl(configurationProvider, siteStore, siteValidation);
        when(siteValidation.validate(any(Site.class))).thenReturn(new ValidationResponse(true, ""));
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
        when(configurationProvider.getConfiguration()).thenReturn(
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, null));

        Site site = new Site(name, alias);
        var siteService = new SiteServiceImpl(configurationProvider, siteStore, siteValidation);
        when(siteValidation.validate(any(Site.class))).thenReturn(new ValidationResponse(false, "name"));
        //Act + Assert
        assertThrows(ValidationException.class, () -> siteService.save(site),
                "Expecting save to throw validation exception");
        Mockito.verify(siteStore, Mockito.times(0)).saveEntity(any(Site.class));
    }

    @Test
    void TestSaveSite_WhenValidSiteWithValidParent_SavesToStore() {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP,
                        SITE_CONFIGURATION_LIST));

        final var root = new Site("root-id","Root", "root", null, "CCO");
        final var site = new Site(null, "name", "alias", root.getSiteId(), "REGION");
        final var siteService = new SiteServiceImpl(configurationProvider, siteStore, siteValidation);
        when(siteValidation.validate(any(Site.class))).thenReturn(new ValidationResponse(true, ""));
        when(siteStore.findById(root.getSiteId())).thenReturn(Optional.of(root));
        when(siteStore.saveEntity(any(Site.class))).thenReturn("123");
        //Act + Assert
        assertThat(siteService.save(site), equalTo("123"));
        Mockito.verify(siteStore, Mockito.times(1)).saveEntity(any(Site.class));
    }

    @Test
    void TestSaveSite_WhenValidSiteWithNoParentAndNoRoot_SavesToStore() {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, null));

        final var site = new Site(null, "name", "alias", null, "root");
        final var siteService = new SiteServiceImpl(configurationProvider, siteStore, siteValidation);
        when(siteValidation.validate(any(Site.class))).thenReturn(new ValidationResponse(true, ""));
        when(siteStore.findRoot()).thenReturn(Optional.empty());
        when(siteStore.saveEntity(any(Site.class))).thenReturn("123");
        //Act + Assert
        assertThat(siteService.save(site), equalTo("123"));
        Mockito.verify(siteStore, Mockito.times(1)).saveEntity(any(Site.class));
    }

    @Test
    void TestSaveSite_WhenValidSiteWithNoParentAndHasRoot_ThrowsValidationException_DoesntSavesToStore() {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, null));

        final var site = new Site(null, "name", "alias", null, "root");
        final var siteService = new SiteServiceImpl(configurationProvider, siteStore, siteValidation);
        when(siteValidation.validate(any(Site.class))).thenReturn(new ValidationResponse(true, ""));
        when(siteStore.findRoot()).thenReturn(Optional.of(new Site()));
        //Act + Assert
        assertThrows(ValidationException.class, () -> siteService.save(site),
                "Root site already exists");
        Mockito.verify(siteStore, Mockito.times(0)).saveEntity(any(Site.class));
    }

    @Test
    void TestSiteServiceImpl_WhenNullValues_ThrowsInitialisationError() {
        // Arrange + Act + Assert
        assertThrows(InitialisationError.class, () -> new SiteServiceImpl(configurationProvider, null, siteValidation),
                "null store should throw");
        assertThrows(InitialisationError.class, () -> new SiteServiceImpl(configurationProvider, siteStore, null),
                "null validation should throw");
    }

    @Test
    void TestGetSites_WhenEmpty_ThrowsInvariantException() {
        // arrange
        when(configurationProvider.getConfiguration()).thenReturn(
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, null));

        final var siteService = new SiteServiceImpl(configurationProvider, siteStore, siteValidation);
        when(siteStore.findAll()).thenReturn(Collections.emptyList());
        // act + assert
        assertThrows(InvariantException.class, siteService::findSites,
                "Expecting getSites to throw invariant exception");
    }

    @Test
    void TestGetSites_WhenStoreHasSites_ReturnsSites() {
        // arrange
        when(configurationProvider.getConfiguration()).thenReturn(
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, null));

        final var siteService = new SiteServiceImpl(configurationProvider, siteStore, siteValidation);
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
    void TestFindSiteByName_WhenStoreHasSite_ReturnsSite() {
        // arrange
        when(configurationProvider.getConfiguration()).thenReturn(
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, null));

        final var siteService = new SiteServiceImpl(configurationProvider, siteStore, siteValidation);
        final var site = new Site("CCO", "Root", null);
        when(siteStore.findByName(site.getName())).thenReturn(Optional.of(site));
        // act
        String siteName = "CCO";
        final Optional<Site> siteFound = siteService.findSiteByName(siteName);
        // assert
        assertThat(siteFound.isPresent(), is(true));
        if(siteFound.isPresent()) {
            assertThat(siteFound.get().getName(), equalTo(site.getName()));
            assertThat(siteFound.get().getAlias(), equalTo(site.getAlias()));
        }
    }

    @Test
    void TestSaveSite_WhenStoreHasSite_ThrowsSiteExistsException() {
        // arrange
        when(configurationProvider.getConfiguration()).thenReturn(
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, null));

        final var siteService = new SiteServiceImpl(configurationProvider, siteStore, siteValidation);
        final var site = new Site("CCO", "Root", null);
        when(siteValidation.validate(site)).thenReturn(new ValidationResponse(true, ""));
        when(siteStore.findByName(site.getName())).thenReturn(Optional.of(site));

        // assert
        assertThrows(ValidationException.class, () -> siteService.save(site),
                "Site Already Exists");
    }

    @Test
    void TestFindSiteByName_WhenStoreHasNoSite_ReturnsEmpty() {
        // arrange
        when(configurationProvider.getConfiguration()).thenReturn(
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, null));

        final var siteService = new SiteServiceImpl(configurationProvider, siteStore, siteValidation);
        when(siteStore.findByName(anyString())).thenReturn(Optional.empty());
        // act
        String siteName = "CCO";
        final Optional<Site> siteFound = siteService.findSiteByName(siteName);
        assertThat(siteFound.isEmpty(), is(true));
    }

    @Test
    void TestFindSiteById_WhenStoreHasSite_ReturnsSite() {
        // arrange
        when(configurationProvider.getConfiguration()).thenReturn(
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, null));

        final var siteService = new SiteServiceImpl(configurationProvider, siteStore, siteValidation);
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
        when(configurationProvider.getConfiguration()).thenReturn(
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, null));

        final var siteService = new SiteServiceImpl(configurationProvider, siteStore, siteValidation);
        when(siteStore.findById(anyString())).thenReturn(Optional.empty());
        // act and assert
        assertThrows(ResponseStatusException.class, () -> siteService.findSiteById("the-id"));
    }

    @Test
    void TestFindRootSite_WhenStoreHasRoot_ReturnsRoot() {
        // arrange
        when(configurationProvider.getConfiguration()).thenReturn(
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, null));

        final var siteService = new SiteServiceImpl(configurationProvider, siteStore, siteValidation);
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
        when(configurationProvider.getConfiguration()).thenReturn(
                new SiteConfiguration("Organization", "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, null));

        final var siteService = new SiteServiceImpl(configurationProvider, siteStore, siteValidation);
        when(siteStore.findRoot()).thenReturn(Optional.empty());
        // act and assert
        assertThrows(ResponseStatusException.class, siteService::findRootSite);
    }

}
