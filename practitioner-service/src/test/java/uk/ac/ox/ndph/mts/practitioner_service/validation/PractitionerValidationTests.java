package uk.ac.ox.ndph.mts.practitioner_service.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.converter.ConvertWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import java.util.List;
import org.mockito.Mock;
import org.junit.jupiter.api.Assertions;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerAttributeConfiguration;
import uk.ac.ox.ndph.mts.practitioner_service.NullableConverter;
import uk.ac.ox.ndph.mts.practitioner_service.configuration.PractitionerConfigurationProvider;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PractitionerValidationTests {

    @Mock
    private PractitionerConfigurationProvider configurationProvider;

    private static List<PractitionerAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP = List.of(
        new PractitionerAttributeConfiguration("givenName", "Given Name", "^[a-zA-Z]{1,35}$"),
        new PractitionerAttributeConfiguration("familyName", "Family Name", "^[a-zA-Z]{1,35}$"),
        new PractitionerAttributeConfiguration("prefix", "Prefix", "^[a-zA-Z]{1,35}$"));

    private static List<PractitionerAttributeConfiguration> PREFIX_NOT_REQUIRED_REGEX_MAP = List.of(
        new PractitionerAttributeConfiguration("givenName", "Given Name", "^[a-zA-Z]{1,35}$"),
        new PractitionerAttributeConfiguration("familyName", "Family Name", "^[a-zA-Z]{1,35}$"),
        new PractitionerAttributeConfiguration("prefix", "Prefix", "^[a-zA-Z]{0,35}$"));

    private static List<PractitionerAttributeConfiguration> PREFIX_EMPTY_REGEX_MAP  = List.of(
        new PractitionerAttributeConfiguration("givenName", "Given Name", "^[a-zA-Z]{1,35}$"),
        new PractitionerAttributeConfiguration("familyName", "Family Name", "^[a-zA-Z]{1,35}$"),
        new PractitionerAttributeConfiguration("prefix", "Prefix", ""));


    private static List<PractitionerAttributeConfiguration> ALL_EMPTY_REGEX_MAP = List.of(
        new PractitionerAttributeConfiguration("givenName", "Given Name", ""),
        new PractitionerAttributeConfiguration("familyName", "Family Name", ""),
        new PractitionerAttributeConfiguration("prefix", "Prefix", ""));

    private static List<PractitionerAttributeConfiguration> INCOMNPLETE_MAP = List.of(
                new PractitionerAttributeConfiguration("givenName", "Given Name", ""));

    private static List<PractitionerAttributeConfiguration> ERROR_MAP = List.of(
        new PractitionerAttributeConfiguration("givenName", "Given Name",""),
        new PractitionerAttributeConfiguration("familyName", "Family Name",""),
        new PractitionerAttributeConfiguration("wrongname", "Given Name",""));


    @ParameterizedTest
    @CsvSource({ ",,,Prefix", ",test,test,Prefix", "test,,test,Given Name", ",test,,Prefix", "null,null,null,Prefix", "test,null,test,Given Name" })
    void TestValidate_WhenFieldsAreEmptyOrNull_ThrowsValidationException(
            @ConvertWith(NullableConverter.class) String prefix, @ConvertWith(NullableConverter.class) String givenName,
            @ConvertWith(NullableConverter.class) String familyName, @ConvertWith(NullableConverter.class) String expectedField) {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
            "Practitioner", ALL_REQUIRED_UNDER_35_MAP));
        Practitioner practitioner = new Practitioner(null, prefix, givenName, familyName, "userAccountId");
        var practitionerValidation = new PractitionerValidation(configurationProvider);

        // Act + Assert
        var result = practitionerValidation.validate(practitioner);
        assertThat(result.isValid(), is(false));
        assertThat(result.getErrorMessage(), containsString(expectedField));
    }

    @Test
    void TestPractitionerValidation_WhenInitWithIncompleteConfig_ThrowsRuntimeException() {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
            "Practitioner", INCOMNPLETE_MAP));

        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new PractitionerValidation(configurationProvider),
                "Expecting incomplete configuration to throw");
    }

    @Test
    void TestPractitionerValidation_WhenInitWithInvalidConfig_ThrowsRuntimeException() {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
            "Practitioner", ERROR_MAP));

        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new PractitionerValidation(configurationProvider),
                "Expecting incomplete configuration to throw");
    }

    @Test
    void TestValidate_WhenValidPractitioner_ReturnsValidResponse() {
        // Arrange
        when(configurationProvider.getConfiguration())
                .thenReturn(new PractitionerConfiguration("person", "Practitioner", ALL_REQUIRED_UNDER_35_MAP));
        var practitionerValidation = new PractitionerValidation(configurationProvider);
        Practitioner practitioner = new Practitioner(null, "prefix", "givenName", "familyName", "userAccountId");

        // Act
        var result = practitionerValidation.validate(practitioner);
        // Assert
        assertThat(result.isValid(), is(true));
    }

    @Test
    void TestValidate_WhenPractitionerWithEmptyPrefix_ReturnsValidResponse() {
        // Arrange
        when(configurationProvider.getConfiguration())
                .thenReturn(new PractitionerConfiguration("person", "Practitioner", PREFIX_NOT_REQUIRED_REGEX_MAP));
        var practitionerValidation = new PractitionerValidation(configurationProvider);
        Practitioner practitioner = new Practitioner(null, "", "givenName", "familyName", "userAccountId");

        // Act
        var result = practitionerValidation.validate(practitioner);
        // Assert
        assertThat(result.isValid(), is(true));
    }

    @Test
    void TestValidate_WhenPractitionerWithEmptyPrefixAndNullRegex_ReturnsValidResponse() {
        // Arrange
        when(configurationProvider.getConfiguration())
                .thenReturn(new PractitionerConfiguration("person", "Practitioner", PREFIX_EMPTY_REGEX_MAP));
        var practitionerValidation = new PractitionerValidation(configurationProvider);
        Practitioner practitioner = new Practitioner(null, "", "givenName", "familyName", "userAccountId");

        // Act
        var result = practitionerValidation.validate(practitioner);
        // Assert
        assertThat(result.isValid(), is(true));
    }

    @Test
    void TestValidate_WhenPractitionerWithEmptyPrefixAndAllNullRegex_ReturnsValidResponse() {
        // Arrange
        when(configurationProvider.getConfiguration())
                .thenReturn(new PractitionerConfiguration("person", "Practitioner", ALL_EMPTY_REGEX_MAP));
        var practitionerValidation = new PractitionerValidation(configurationProvider);
        Practitioner practitioner = new Practitioner(null, "", "givenName", "familyName", "userAccountId");

        // Act
        var result = practitionerValidation.validate(practitioner);
        // Assert
        assertThat(result.isValid(), is(true));
    }

    @Test
    void TestValidate_WhenPractitionerWithNullPrefix_ReturnsValidResponse() {
        // Arrange
        when(configurationProvider.getConfiguration())
                .thenReturn(new PractitionerConfiguration("person", "Practitioner", PREFIX_NOT_REQUIRED_REGEX_MAP));
        var practitionerValidation = new PractitionerValidation(configurationProvider);
        Practitioner practitioner = new Practitioner(null, null, "givenName", "familyName", "userAccountId");

        // Act
        var result = practitionerValidation.validate(practitioner);
        // Assert
        assertThat(result.isValid(), is(true));
    }
}
