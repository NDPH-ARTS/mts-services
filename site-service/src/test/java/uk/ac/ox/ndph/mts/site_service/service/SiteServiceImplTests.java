package uk.ac.ox.ndph.mts.site_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.site_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.site_service.exception.InvariantException;
import uk.ac.ox.ndph.mts.site_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SiteServiceImplTests {

    @Mock
    private EntityStore<String, Site> siteStore;

    @Mock
    private ModelEntityValidation<Site> siteValidation;

    @Captor
    ArgumentCaptor<Site> siteCaptor;

    @Test
    void TestSaveSiteParent_WithSiteParent_ValidatesSiteParent() {
        // Arrange
        String name = "name";
        String alias = "alias";
        String parent = "parent";

        Site siteWithParent = new Site(name, alias, parent);
        var siteService = new SiteServiceImpl(siteStore, siteValidation);
        when(siteValidation.validate(any(Site.class))).thenReturn(new ValidationResponse(true, ""));
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
        Site site = new Site(name, alias);
        var siteService = new SiteServiceImpl(siteStore, siteValidation);
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
        Site site = new Site(name, alias);
        var siteService = new SiteServiceImpl(siteStore, siteValidation);
        when(siteValidation.validate(any(Site.class))).thenReturn(new ValidationResponse(false, "name"));
        //Act + Assert
        Assertions.assertThrows(ValidationException.class, () -> siteService.save(site),
                "Expecting save to throw validation exception");
        Mockito.verify(siteStore, Mockito.times(0)).saveEntity(any(Site.class));
    }

    @Test
    void TestSiteServiceImpl_WhenNullValues_ThrowsInitialisationError() {
        // Arrange + Act + Assert
        Assertions.assertThrows(InitialisationError.class, () -> new SiteServiceImpl(null, siteValidation),
                "null store should throw");
        Assertions.assertThrows(InitialisationError.class, () -> new SiteServiceImpl(siteStore, null),
                "null validation should throw");
    }

    @Test
    void TestGetSites_WhenEmpty_ThrowsInvariantException() {
        // arrange
        final var siteService = new SiteServiceImpl(siteStore, siteValidation);
        when(siteStore.findAll()).thenReturn(Collections.emptyList());
        // act + assert
        Assertions.assertThrows(InvariantException.class, () -> siteService.findSites(),
                "Expecting getSites to throw invariant exception");
    }

    @Test
    void TestGetSites_WhenStoreHasSites_ReturnsSites() {
        // arrange
        final var siteService = new SiteServiceImpl(siteStore, siteValidation);
        final var site = new Site("CCO", "Root", null);
        when(siteStore.findAll()).thenReturn(Collections.singletonList(site));
        // act
        final List<Site> sites = siteService.findSites();
        // assert
        assertThat(sites, is(not(empty())));

        // TODO: find fix for
        // java.lang.NoSuchMethodError:
        // 'boolean org.hamcrest.beans.SamePropertyValuesAs.isNotNull(java.lang.Object, org.hamcrest.Description)'
        //assertThat(sites, hasItem(samePropertyValuesAs(site)));
    }

    @Test
    void TestFindSiteByName_WhenStoreHasSite_ReturnsSite() {
        // arrange
        final var siteService = new SiteServiceImpl(siteStore, siteValidation);
        final var site = new Site("CCO", "Root", null);
        when(siteStore.findByName(eq(site.getName()))).thenReturn(Optional.of(site));
        // act
        String siteName = "CCO";
        final Optional<Site> siteFound = siteService.findSiteByName(siteName);
        // assert
        assertThat(siteFound.isPresent(), is(true));
        assertThat(siteFound.get().getName(), equalTo(site.getName()));
        assertThat(siteFound.get().getAlias(), equalTo(site.getAlias()));
    }

    @Test
    void TestSaveSite_WhenStoreHasSite_ThrowsSiteExistsException() {
        // arrange
        final var siteService = new SiteServiceImpl(siteStore, siteValidation);
        final var site = new Site("CCO", "Root", null);
        when(siteValidation.validate(site)).thenReturn(new ValidationResponse(true, ""));
        when(siteStore.findByName(eq(site.getName()))).thenReturn(Optional.of(site));

        // assert
        Assertions.assertThrows(ValidationException.class, () -> siteService.save(site),
                "Site Already Exists");
    }

    @Test
    void TestFindSiteByName_WhenStoreHasNoSite_ReturnsEmpty() {
        // arrange
        final var siteService = new SiteServiceImpl(siteStore, siteValidation);
        when(siteStore.findByName(anyString())).thenReturn(Optional.empty());
        // act
        String siteName = "CCO";
        final Optional<Site> siteFound = siteService.findSiteByName(siteName);
        assertThat(siteFound.isEmpty(), is(true));
    }

}
