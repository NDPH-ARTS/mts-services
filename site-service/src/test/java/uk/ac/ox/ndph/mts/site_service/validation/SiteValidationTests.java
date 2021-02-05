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
        new SiteAttributeConfiguration("alias", "Alias", "^[a-zA-Z]{1,35}$"));

    private static final List<SiteAttributeConfiguration> ALL_EMPTY_REGEX_MAP = List.of(
        new SiteAttributeConfiguration("name", "Name", ""),
        new SiteAttributeConfiguration("alias", "Alias", ""));

    private static final List<SiteAttributeConfiguration> INCOMNPLETE_MAP = List.of(
                new SiteAttributeConfiguration("name", "Name", ""));

    private static final List<SiteAttributeConfiguration> ERROR_MAP = List.of(
        new SiteAttributeConfiguration("wrongname", "WrongName",""),
        new SiteAttributeConfiguration("alias", "Alias",""));
    
    
    @ParameterizedTest
    @CsvSource({ ",,Name", ",test,Name", "test,,Alias", "test,null,Alias", "null,null,Name", "null,test,Name" })
    void TestValidate_WhenFieldsAreEmptyOrNull_ThrowsValidationException(
            @ConvertWith(NullableConverter.class) String name,
            @ConvertWith(NullableConverter.class) String alias,
			@ConvertWith(NullableConverter.class) String expectedField) {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
            "Site", ALL_REQUIRED_UNDER_35_MAP));
        Site site = new Site(name, alias);
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
            "Site", INCOMNPLETE_MAP));

        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new SiteValidation(configurationProvider),
                "Expecting incomplete configuration to throw");
    }

    @Test
    void TestSiteValidation_WhenInitWithInvalidConfig_ThrowsRuntimeException() {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
            "Site", ERROR_MAP));

        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new SiteValidation(configurationProvider),
                "Expecting incomplete configuration to throw");
    }

    @Test
    void TestValidate_WhenValidSite_ReturnsValidReponse() {
        // Arrange
        String name = "name";
        String alias = "alias";
        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site",
            "Site", ALL_REQUIRED_UNDER_35_MAP));
        var siteValidation = new SiteValidation(configurationProvider);
        Site site = new Site(name, alias);

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
        when(configurationProvider.getConfiguration()).thenReturn(new SiteConfiguration("site", "Site",
            ALL_EMPTY_REGEX_MAP));
        var siteValidation = new SiteValidation(configurationProvider);
        Site site = new Site(name, alias);

        // Act
        var result = siteValidation.validate(site);
        // Assert
        assertThat(result.isValid(), is(true));
    }

}
