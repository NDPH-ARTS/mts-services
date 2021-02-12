package uk.ac.ox.ndph.mts.site_service.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.site_service.NullableConverter;
import uk.ac.ox.ndph.mts.site_service.configuration.SiteConfigurationProvider;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.SiteAttributeConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SiteValidationTests {

    @Mock
    private SiteConfigurationProvider configurationProvider;

    private static final List<SiteAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP = List.of(
        new SiteAttributeConfiguration("name", "Name", "^[a-zA-Z]{1,35}$"),
        new SiteAttributeConfiguration("alias", "Alias", "^[a-zA-Z]{1,35}$"),
        new SiteAttributeConfiguration("parentSiteId", "Parent Site Id", "^[a-zA-Z]{1,35}$"),
        new SiteAttributeConfiguration("siteType", "Site Type", "^[a-zA-Z]{1,35}$"));

    private static final List<SiteAttributeConfiguration> ALL_EMPTY_REGEX_MAP = List.of(
        new SiteAttributeConfiguration("name", "Name", ""),
        new SiteAttributeConfiguration("alias", "Alias", ""),
        new SiteAttributeConfiguration("parentSiteId", "Parent Site Id", ""),
        new SiteAttributeConfiguration("siteType", "Site Type", ""));

    private static final List<SiteAttributeConfiguration> INCOMPLETE_MAP = List.of(
                new SiteAttributeConfiguration("name", "Name", ""));

    private static final List<SiteAttributeConfiguration> INCOMPLETE_MAP_A = List.of(
            new SiteAttributeConfiguration("name", "Name", ""),
            new SiteAttributeConfiguration("alias", "Alias", ""));

    private static final List<SiteAttributeConfiguration> INCOMPLETE_MAP_B = List.of(
            new SiteAttributeConfiguration("name", "Name", ""),
            new SiteAttributeConfiguration("alias", "Alias", ""),
            new SiteAttributeConfiguration("parentSiteId", "Parent Site Id", ""));

    private static final List<SiteAttributeConfiguration> ERROR_MAP = List.of(
        new SiteAttributeConfiguration("wrongname", "WrongName",""),
        new SiteAttributeConfiguration("alias", "Alias",""));

    private static final List<SiteConfiguration> SITE_CONFIGURATION_LIST  = List.of(
            new SiteConfiguration("Organization", "site", "REGION", ALL_REQUIRED_UNDER_35_MAP,
                    Collections.singletonList(new SiteConfiguration("Organization", "site", "COUNTRY", ALL_REQUIRED_UNDER_35_MAP,
                            Collections.singletonList(new SiteConfiguration("Organization", "site", "LCC", ALL_REQUIRED_UNDER_35_MAP, null)
                            )))));

    @ParameterizedTest
    @CsvSource({ ",,,testType,Name", ",test,,,Name", "test,,testId,testType,Alias",
                "test,null,testId,testType,Alias", "null,null,testId,testType,Name", "null,test,testId,testType,Name",
                "test,test,null,testType,Parent Site Id", "test,test,testId,null,Site Type" })
    void TestValidate_WhenFieldsAreEmptyOrNull_ThrowsValidationException(
            @ConvertWith(NullableConverter.class) String name,
            @ConvertWith(NullableConverter.class) String alias,
            @ConvertWith(NullableConverter.class) String parentSiteId,
            @ConvertWith(NullableConverter.class) String siteType,
            @ConvertWith(NullableConverter.class) String expectedField) {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
            "Site", "CCO", ALL_REQUIRED_UNDER_35_MAP, SITE_CONFIGURATION_LIST));
        Site site = new Site(name, alias, parentSiteId, siteType);
        var siteValidation = new SiteValidation(configurationProvider); 

        // Act + Assert
        var result = siteValidation.validate(site);
        assertThat(result.isValid(), is(false));
        assertThat(result.getErrorMessage(), containsString(expectedField));
    }

    @Test
    void TestSiteValidation_WhenInitWithIncompleteConfig_ThrowsRuntimeException() {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
            "Site", "CCO", INCOMPLETE_MAP, SITE_CONFIGURATION_LIST));

        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new SiteValidation(configurationProvider),
                "Expecting incomplete configuration to throw");

        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
                "Site", "CCO", INCOMPLETE_MAP_A, SITE_CONFIGURATION_LIST));

        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new SiteValidation(configurationProvider),
                "Expecting incomplete configuration to throw");

        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
                "Site", "CCO", INCOMPLETE_MAP_B, SITE_CONFIGURATION_LIST));

        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new SiteValidation(configurationProvider),
                "Expecting incomplete configuration to throw");
    }

    @Test
    void TestSiteValidation_WhenInitWithInvalidConfig_ThrowsRuntimeException() {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
            "Site", "CCO", ERROR_MAP, SITE_CONFIGURATION_LIST));

        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new SiteValidation(configurationProvider),
                "Expecting incomplete configuration to throw");
    }

    @Test
    void TestValidate_WhenValidSite_ReturnsValidReponse() {
        // Arrange
        String name = "name";
        String alias = "alias";
        String parentSiteId = "parentSiteId";
        String siteType = "siteType";

        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
            "Site", "CCO", ALL_REQUIRED_UNDER_35_MAP, SITE_CONFIGURATION_LIST));
        var siteValidation = new SiteValidation(configurationProvider);
        Site site = new Site(name, alias, parentSiteId, siteType);

        // Act
        var result = siteValidation.validate(site);
        // Assert
        assertThat(result.isValid(), is(true));
    }

    @Test
    void TestValidate_WhenSiteWithAllNullRegex_ReturnsValidReponse() {
        // Arrange
        String name = "name";
        String alias = "alias";
        String parentSiteId = "parentSiteId";
        String siteType = "siteType";
        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("Organization",
            "site","CCO", ALL_EMPTY_REGEX_MAP, SITE_CONFIGURATION_LIST));
        var siteValidation = new SiteValidation(configurationProvider);
        Site site = new Site(name, alias, parentSiteId, siteType);

        // Act
        var result = siteValidation.validate(site);
        // Assert
        assertThat(result.isValid(), is(true));
    }

}
