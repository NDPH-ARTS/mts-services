package uk.ac.ox.ndph.mts.site_service.service;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.site_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.site_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.ValidationResponse;
import uk.ac.ox.ndph.mts.site_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.site_service.repository.HapiFhirRepository;
import uk.ac.ox.ndph.mts.site_service.validation.ModelEntityValidation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SiteServiceImplTests {

    @Mock
    private EntityStore<Site> siteStore;

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
        when(siteValidation.validate(siteWithParent)).thenReturn(new ValidationResponse(true, ""));
        when(siteStore.saveEntity(siteWithParent)).thenReturn("123");

        //Act
        String result = siteService.save(siteWithParent);
        assertThat(result, equalTo("123"));

        //Assert
        Mockito.verify(siteValidation).validate(siteCaptor.capture());
        var value = siteCaptor.getValue();
        assertThat(siteWithParent, equalTo(value));
    }

    @Test
    void TestSaveSite_WhenValidSite_SavesToStore(){
        // Arrange
        String name = "name";
        String alias = "alias";
        Site site = new Site(name, alias);
        var siteService = new SiteServiceImpl(siteStore, siteValidation);
        when(siteValidation.validate(site)).thenReturn(new ValidationResponse(true, ""));
        when(siteStore.saveEntity(site)).thenReturn("123");
        //Act
        siteService.save(site);

        //Assert
        Mockito.verify(siteStore).saveEntity(siteCaptor.capture());
        var value = siteCaptor.getValue();
        assertThat(site, equalTo(value));
    }

    @Test
    void TestSaveSite_WhenInvalidSite_ThrowsValidationException_DoesntSavesToStore(){
        // Arrange
        String name = "name";
        String alias = "alias";
        Site site = new Site(name, alias);
        var siteService = new SiteServiceImpl(siteStore, siteValidation);
        when(siteValidation.validate(site)).thenReturn(new ValidationResponse(false, "name"));
        //Act + Assert
        Assertions.assertThrows(ValidationException.class, () -> siteService.save(site),
                "Expecting save to throw validation exception");
        Mockito.verify(siteStore, Mockito.times(0)).saveEntity(site);
    }

    @Test
    void TestSiteServiceImpl_WhenNullValues_ThrowsInitialisationError(){
        // Arrange + Act + Assert
        Assertions.assertThrows(InitialisationError.class, () -> new SiteServiceImpl(null, siteValidation),
                "null store should throw");
        Assertions.assertThrows(InitialisationError.class, () -> new SiteServiceImpl(siteStore, null),
                "null validation should throw");
    }

    @Test
    void TestFindtSiteByName_WhenStoreHasSite_ReturnsSite() {
        // arrange
        final var siteService = new SiteServiceImpl(siteStore, siteValidation);
        final var site = new Site("CCO", "Root", null);
        when(siteStore.findOrganizationByName(anyString())).thenReturn(site);

        // act
        String siteName = "CCO";
        final Site siteFound = siteService.findSiteByName(siteName);

        // assert
        assertThat(siteFound.getName(), equalTo(site.getName()));
        assertThat(siteFound.getAlias(), equalTo(site.getAlias()));
    }

    @Test
    void TestFindtSiteByName_WhenStoreHasNoSite_ReturnsNull() {
        // arrange
        final var siteService = new SiteServiceImpl(siteStore, siteValidation);
        when(siteStore.findOrganizationByName(anyString())).thenReturn(null);

        // act
        String siteName = "CCO";
        final Site siteFound = siteService.findSiteByName(siteName);
        assertThat(siteFound, equalTo(null));
    }


}
