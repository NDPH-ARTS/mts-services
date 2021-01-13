package uk.ac.ox.ndph.mts.site_service.service;

import org.hl7.fhir.r4.model.ResearchStudy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.converter.ConvertWith;

import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.Captor;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.Assertions;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.repository.FhirRepository;
import uk.ac.ox.ndph.mts.site_service.exception.RestException;
import uk.ac.ox.ndph.mts.site_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.site_service.NullableConverter;
import uk.ac.ox.ndph.mts.site_service.configuration.SiteAttribute;
import uk.ac.ox.ndph.mts.site_service.configuration.SiteConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SiteServiceTests {

    @Mock
    private FhirRepository fhirRepository;

    @Mock
    private SiteConfigurationProvider configurationProvider;

    @Captor
    ArgumentCaptor<org.hl7.fhir.r4.model.Organization> organizationCaptor;

    private static List<SiteAttribute> ALL_REQUIRED_UNDER_35_MAP;
    static {
        ALL_REQUIRED_UNDER_35_MAP = new LinkedList<SiteAttribute>();
        ALL_REQUIRED_UNDER_35_MAP.add(new SiteAttribute("name", "Name", "^[a-zA-Z]{1,35}$"));
        ALL_REQUIRED_UNDER_35_MAP.add(new SiteAttribute("alias", "Alias", "^[a-zA-Z]{1,35}$"));

    }
    
    private static List<SiteAttribute> PREFIX_NOT_REQUIRED_REGEX_MAP;
    static {
        PREFIX_NOT_REQUIRED_REGEX_MAP = new LinkedList<SiteAttribute>();
        PREFIX_NOT_REQUIRED_REGEX_MAP.add(new SiteAttribute("name", "Name", "^[a-zA-Z]{1,35}$"));
        PREFIX_NOT_REQUIRED_REGEX_MAP.add(new SiteAttribute("alias", "Alias", "^[a-zA-Z]{1,35}$"));

    }
    
    private static List<SiteAttribute> PREFIX_EMPTY_REGEX_MAP;
    static {
        PREFIX_EMPTY_REGEX_MAP = new LinkedList<SiteAttribute>();
        PREFIX_EMPTY_REGEX_MAP.add(new SiteAttribute("name", "Name", "^[a-zA-Z]{1,35}$"));
        PREFIX_EMPTY_REGEX_MAP.add(new SiteAttribute("alias", "Alias", "^[a-zA-Z]{1,35}$"));
    }
    
    private static List<SiteAttribute> ALL_EMPTY_REGEX_MAP;
    static {
        ALL_EMPTY_REGEX_MAP = new LinkedList<SiteAttribute>();
        ALL_EMPTY_REGEX_MAP.add(new SiteAttribute("name", "Name", ""));
        ALL_EMPTY_REGEX_MAP.add(new SiteAttribute("alias", "Alias", ""));
    }

    private static List<SiteAttribute> INCOMNPLETE_MAP;
    static {
        INCOMNPLETE_MAP = new LinkedList<SiteAttribute>();
        INCOMNPLETE_MAP.add(new SiteAttribute("name", "Name", ""));
    }

    private static List<SiteAttribute> ERROR_MAP;
    static {
        ERROR_MAP = new LinkedList<SiteAttribute>();
        ERROR_MAP.add(new SiteAttribute("name", "Name",""));
        ERROR_MAP.add(new SiteAttribute("alias", "Alias",""));
        ERROR_MAP.add(new SiteAttribute("wrongname", "Name",""));
    }
    
    @ParameterizedTest
    @CsvSource({ ",",  ",test", "test,", "null,null", "null,test" })
    void TestSaveSite_WhenFieldsAreEmptyOrNull_ThrowsArgumentException(
            @ConvertWith(NullableConverter.class) String name,
            @ConvertWith(NullableConverter.class) String alias) {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
            "Site", ALL_REQUIRED_UNDER_35_MAP));

        EntityService entityService = new SiteService(fhirRepository, configurationProvider);
        Site site = new Site(name, alias);

        // Act + Assert
        Assertions.assertThrows(ValidationException.class, () -> entityService.saveSite(site),
                "Expecting empty fields to throw");
    }

    @Test
    void TestSiteService_WhenInitWithIncompleteConfig_ThrowsRuntimeException() {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
            "Site", INCOMNPLETE_MAP));

        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new SiteService(fhirRepository, configurationProvider),
                "Expecting incomplete configuration to throw");
    }

    @Test
    void TestSaveSite_WhenSaveSite_SaveSiteToRepository() {
        // Arrange
        String name = "name";
        String alias = "alias";
        String researchStudy = "123";
        ResearchStudy mockRS = new ResearchStudy();
        mockRS.setId(researchStudy);

        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
            "Site", ALL_REQUIRED_UNDER_35_MAP));
        when(fhirRepository.saveResearchStudy(Mockito.any(org.hl7.fhir.r4.model.ResearchStudy.class)))
                .thenReturn(mockRS);

        EntityService entityService = new SiteService(fhirRepository, configurationProvider);
        Site site = new Site(name, alias);

        // Act
        entityService.saveSite(site);

        // Assert
        Mockito.verify(fhirRepository).saveOrganization(organizationCaptor.capture());
        org.hl7.fhir.r4.model.Organization value = organizationCaptor.getValue();
        Assertions.assertEquals(name, value.getName());
        Assertions.assertEquals(alias, value.getAlias().get(0).getValue());
        Assertions.assertEquals(researchStudy, value.getPartOf().getResource().getIdElement().getValue());
    }

    @Test
    void TestSaveSite_WhenSaveSiteWithEmtpyPrefix_SaveSiteToRepository() {
        // Arrange
        String name = "name";
        String alias = "alias";

        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
        "Site", PREFIX_NOT_REQUIRED_REGEX_MAP));

        EntityService entityService = new SiteService(fhirRepository, configurationProvider);
        Site site = new Site(name, alias);

        // Act
        entityService.saveSite(site);

        // Assert
        Mockito.verify(fhirRepository).saveOrganization(organizationCaptor.capture());
        org.hl7.fhir.r4.model.Organization value = organizationCaptor.getValue();
        Assertions.assertEquals(name, value.getName());
        Assertions.assertEquals(alias, value.getAlias().get(0).getValue());
    }

    @Test
    void TestSaveSite_WhenSaveSiteWithEmtpyPrefixAndNullRegex_SaveSiteToRepository() {
        // Arrange
        String name = "name";
        String alias = "alias";

        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
            "Site", PREFIX_EMPTY_REGEX_MAP));

        EntityService entityService = new SiteService(fhirRepository, configurationProvider);
        Site site = new Site(name, alias);

        // Act
        entityService.saveSite(site);

        // Assert
        Mockito.verify(fhirRepository).saveOrganization(organizationCaptor.capture());
        org.hl7.fhir.r4.model.Organization value = organizationCaptor.getValue();
        Assertions.assertEquals(name, value.getName());
        Assertions.assertEquals(alias, value.getAlias().get(0).getValue());
    }

    @Test
    void TestSaveSite_WhenSaveSiteWithEmtpyPrefixAndAllNullRegex_SaveSiteToRepository() {
        // Arrange
        String name = "name";
        String alias = "alias";

        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site", "Site",
            ALL_EMPTY_REGEX_MAP));

        EntityService entityService = new SiteService(fhirRepository, configurationProvider);
        Site site = new Site(name, alias);

        // Act
        entityService.saveSite(site);

        // Assert
        Mockito.verify(fhirRepository).saveOrganization(organizationCaptor.capture());
        org.hl7.fhir.r4.model.Organization value = organizationCaptor.getValue();
        Assertions.assertEquals(name, value.getName());
        Assertions.assertEquals(alias, value.getAlias().get(0).getValue());
    }

    @Test
    void TestSaveSite_WhenSaveSiteWithNullPrefix_SaveSiteToRepository() {
        // Arrange
        String name = "name";
        String alias = "alias";

        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
        "Site", PREFIX_NOT_REQUIRED_REGEX_MAP));

        EntityService entityService = new SiteService(fhirRepository, configurationProvider);
        Site site = new Site(name, alias);

        // Act
        entityService.saveSite(site);

        // Assert
        Mockito.verify(fhirRepository).saveOrganization(organizationCaptor.capture());
        org.hl7.fhir.r4.model.Organization value = organizationCaptor.getValue();
        Assertions.assertEquals(name, value.getName());
        Assertions.assertEquals(alias, value.getAlias().get(0).getValue());
    }

    @Test
    void TestSaveSite_WhenRepositoryThrows_ThrowsSameException() {
        when(fhirRepository.saveOrganization(Mockito.any(org.hl7.fhir.r4.model.Organization.class)))
                .thenThrow(RestException.class);
        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
                "Site", ALL_REQUIRED_UNDER_35_MAP));

        EntityService entityService = new SiteService(fhirRepository, configurationProvider);
        Site site = new Site("name", "alias");

        // Act + Assert
        Assertions.assertThrows(RestException.class, () -> entityService.saveSite(site),
                "Expecting repository error to throw bad gateway");
    }
}
