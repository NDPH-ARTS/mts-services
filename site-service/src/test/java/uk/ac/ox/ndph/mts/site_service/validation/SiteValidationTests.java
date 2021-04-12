package uk.ac.ox.ndph.mts.site_service.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.site_service.NullableConverter;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.SiteAttributeConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class SiteValidationTests {

    private static final List<SiteAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP = List.of(
            new SiteAttributeConfiguration("name", "string", "Name", "^[a-zA-Z\\s]{1,35}$"),
            new SiteAttributeConfiguration("alias", "string","Alias", "^[a-zA-Z\\s]{1,35}$"),
            new SiteAttributeConfiguration("parentSiteId", "string","Parent Site Id", "^[a-zA-Z\\s]{1,35}$"),
            new SiteAttributeConfiguration("siteType", "string","Site Type", "^[a-zA-Z\\s]{1,35}$"));

    private static final List<SiteAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP_CUSTOM = List.of(
            new SiteAttributeConfiguration("address", "address", "Address", ""));

    private static final List<SiteAttributeConfiguration> ALL_EMPTY_REGEX_MAP = List.of(
            new SiteAttributeConfiguration("name", "string","Name", ""),
            new SiteAttributeConfiguration("alias", "string","Alias", ""),
            new SiteAttributeConfiguration("parentSiteId", "string","Parent Site Id", ""),
            new SiteAttributeConfiguration("siteType", "string","Site Type", ""));

    private static final List<SiteAttributeConfiguration> INCOMPLETE_MAP = List.of(
            new SiteAttributeConfiguration("name", "string","Name", ""));

    private static final List<SiteAttributeConfiguration> INCOMPLETE_MAP_A = List.of(
            new SiteAttributeConfiguration("name", "string","Name", ""),
            new SiteAttributeConfiguration("alias", "string","Alias", ""));

    private static final List<SiteAttributeConfiguration> INCOMPLETE_MAP_B = List.of(
            new SiteAttributeConfiguration("name", "string","Name", ""),
            new SiteAttributeConfiguration("alias", "string","Alias", ""),
            new SiteAttributeConfiguration("parentSiteId", "string","Parent Site Id", ""));

    private static final List<SiteAttributeConfiguration> ERROR_MAP = List.of(
            new SiteAttributeConfiguration("wrongname", "string","WrongName", ""),
            new SiteAttributeConfiguration("alias", "string","Alias", ""));

    private static final List<SiteConfiguration> SITE_CONFIGURATION_LIST = List.of(
            new SiteConfiguration("Organization", "site", "REGION", ALL_REQUIRED_UNDER_35_MAP, null,
                    Collections.singletonList(new SiteConfiguration("Organization", "site", "COUNTRY", ALL_REQUIRED_UNDER_35_MAP, null,
                            Collections.singletonList(new SiteConfiguration("Organization", "site", "LCC", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, null)
                            )))));

    @ParameterizedTest
    @CsvSource({",,,testType,Invalid Site", ",,,,Invalid Site", ",,,null,Invalid Site"})
    void TestValidate_WhenSiteInvalidOrEmpty_ThrowsValidationException(
            @ConvertWith(NullableConverter.class) String name,
            @ConvertWith(NullableConverter.class) String alias,
            @ConvertWith(NullableConverter.class) String parentSiteId,
            @ConvertWith(NullableConverter.class) String siteType,
            @ConvertWith(NullableConverter.class) String expectedField) {
        // Arrange
        final SiteConfiguration config = new SiteConfiguration("site",
                "Site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, SITE_CONFIGURATION_LIST);
        Site site = new Site(name, alias, parentSiteId, siteType);
        var siteValidation = new SiteValidation(config);

        // Act + Assert
        var result = siteValidation.validateCoreAttributes(site);
        assertThat(result.isValid(), is(false));
        assertThat(result.getErrorMessage(), containsString(expectedField));
    }

    @ParameterizedTest
    @CsvSource({",,,CCO,Name", ",test,,CCO,Name", "test,,testId,CCO,Alias", "test,null,testId,CCO,Alias",
                "null,null,testId,CCO,Name", "null,test,testId,CCO,Name", "test,test,null,CCO,Parent Site Id"})
    void TestValidate_WhenFieldsAreEmptyOrNull_ThrowsValidationException(
            @ConvertWith(NullableConverter.class) String name,
            @ConvertWith(NullableConverter.class) String alias,
            @ConvertWith(NullableConverter.class) String parentSiteId,
            @ConvertWith(NullableConverter.class) String siteType,
            @ConvertWith(NullableConverter.class) String expectedField) {
        // Arrange
        final SiteConfiguration config = new SiteConfiguration("site",
                "Site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, SITE_CONFIGURATION_LIST);
        Site site = new Site(name, alias, parentSiteId, siteType);
        var siteValidation = new SiteValidation(config);

        // Act + Assert
        var result = siteValidation.validateCoreAttributes(site);
        assertThat(result.isValid(), is(false));
        assertThat(result.getErrorMessage(), containsString(expectedField));
    }

    @Test
    void TestSiteValidation_WhenInitWithIncompleteConfig_ThrowsRuntimeException() {
        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new SiteValidation(null),
            "Expecting null configuration to throw");
        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new SiteValidation(new SiteConfiguration("site",
                        "Site", "CCO", INCOMPLETE_MAP, INCOMPLETE_MAP, SITE_CONFIGURATION_LIST)),
                "Expecting incomplete configuration to throw");
        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new SiteValidation(new SiteConfiguration("site",
                        "Site", "CCO", INCOMPLETE_MAP_A, INCOMPLETE_MAP_A, SITE_CONFIGURATION_LIST)),
                "Expecting incomplete configuration to throw");

        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new SiteValidation(new SiteConfiguration("site",
                        "Site", "CCO", INCOMPLETE_MAP_B, INCOMPLETE_MAP_B, SITE_CONFIGURATION_LIST)),
                "Expecting incomplete configuration to throw");
    }

    @Test
    void TestSiteValidation_WhenInitWithInvalidConfig_ThrowsRuntimeException() {
        // Arrange
        final var config = new SiteConfiguration("site",
                "Site", "CCO", ERROR_MAP, ERROR_MAP, SITE_CONFIGURATION_LIST);

        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new SiteValidation(config),
                "Expecting incomplete configuration to throw");
    }

    @Test
    void TestValidate_WhenValidSite_ReturnsValidResponse() {
        // Arrange
        String name = "name";
        String alias = "alias";
        String parentSiteId = "parentSiteId";
        String siteType = "CCO";

        final var config = new SiteConfiguration("site",
                "Site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, SITE_CONFIGURATION_LIST);
        var siteValidation = new SiteValidation(config);
        Site site = new Site(name, alias, parentSiteId, siteType);

        // Act
        var result = siteValidation.validateCoreAttributes(site);
        // Assert
        assertThat(result.isValid(), is(true));
    }

    @Test
    void TestValidate_WhenSiteWithAllNullRegex_ReturnsValidResponse() {
        // Arrange
        String name = "name";
        String alias = "alias";
        String parentSiteId = "parentSiteId";
        String siteType = "CCO";
        final var config = new SiteConfiguration("Organization",
                "site", "CCO", ALL_EMPTY_REGEX_MAP, ALL_EMPTY_REGEX_MAP, SITE_CONFIGURATION_LIST);
        var siteValidation = new SiteValidation(config);
        Site site = new Site(name, alias, parentSiteId, siteType);

        // Act
        var result = siteValidation.validateCoreAttributes(site);
        // Assert
        assertThat(result.isValid(), is(true));
    }

    @Test
    void TestValidate_WhenSiteWithFieldMaxLength_ReturnsValidResponse() {
        // Arrange
        String name = "name";
        String alias = "Long LongLongLongLongLong Long Long";
        String parentSiteId = "parentSiteId";
        String siteType = "CCO";
        final var config = new SiteConfiguration("Organization",
                "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, SITE_CONFIGURATION_LIST);
        var siteValidation = new SiteValidation(config);
        Site site = new Site(name, alias, parentSiteId, siteType);

        // Act
        var result = siteValidation.validateCoreAttributes(site);

        // Assert
        assertThat(result.isValid(), is(true));
    }

    @ParameterizedTest
    @CsvSource({"name,   ,parentSiteId,siteType,failed validation",
            "name,LongLongLongLongLongLongLongLongLongLongLongLongLong,parentSiteId,siteType,failed validation"
    })
    void TestValidate_WhenSiteWithField_EmptySpaces_LessThanMinLength_ExceedMaxLength_ThrowsException(
            @ConvertWith(NullableConverter.class) String name,
            @ConvertWith(NullableConverter.class) String alias,
            @ConvertWith(NullableConverter.class) String parentSiteId,
            @ConvertWith(NullableConverter.class) String siteType,
            @ConvertWith(NullableConverter.class) String failedValidation) {

        // Arrange
        final var config = new SiteConfiguration("Organization",
                "site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, SITE_CONFIGURATION_LIST);
        var siteValidation = new SiteValidation(config);

        Site site = new Site(name, alias, parentSiteId, siteType);

        // Act + Assert
        var result = siteValidation.validateCoreAttributes(site);
        assertThat(result.isValid(), is(false));
        assertThat(result.getErrorMessage(), containsString(failedValidation));
    }
}
