package uk.ac.ox.ndph.mts.site_service.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
import uk.ac.ox.ndph.mts.site_service.repository.FhirRepository;
import uk.ac.ox.ndph.mts.site_service.validation.ModelEntityValidation;

@ExtendWith(MockitoExtension.class)
class SiteServiceImplTests {

    @Mock
    private FhirRepository repository;

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
        var siteService = new SiteServiceImpl(repository, siteStore, siteValidation);
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
    void TestSaveSite_WhenValidSite_SavesToStore(){
        // Arrange
        String name = "name";
        String alias = "alias";
        Site site = new Site(name, alias);
        var siteService = new SiteServiceImpl(repository, siteStore, siteValidation);
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
    void TestSaveSite_WhenInvalidSite_ThrowsValidationException_DoesntSavesToStore(){
        // Arrange
        String name = "name";
        String alias = "alias";
        Site site = new Site(name, alias);
        var siteService = new SiteServiceImpl(repository, siteStore, siteValidation);
        when(siteValidation.validate(any(Site.class))).thenReturn(new ValidationResponse(false, "name"));
        //Act + Assert
        Assertions.assertThrows(ValidationException.class, () -> siteService.save(site),
                "Expecting save to throw validation exception");
        Mockito.verify(siteStore, Mockito.times(0)).saveEntity(any(Site.class));
    }

    @Test
    void TestSiteServiceImpl_WhenNullValues_ThrowsInitialisationError(){
        // Arrange + Act + Assert
        Assertions.assertThrows(InitialisationError.class, () -> new SiteServiceImpl(null, siteStore, siteValidation),
                "null repository should throw");
        Assertions.assertThrows(InitialisationError.class, () -> new SiteServiceImpl(repository,null, siteValidation),
                "null store should throw");
        Assertions.assertThrows(InitialisationError.class, () -> new SiteServiceImpl(repository, siteStore, null),
                "null validation should throw");
    }
}
