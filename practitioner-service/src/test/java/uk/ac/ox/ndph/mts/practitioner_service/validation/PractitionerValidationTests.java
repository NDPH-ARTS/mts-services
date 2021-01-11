package uk.ac.ox.ndph.mts.practitioner_service.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.converter.ConvertWith;

import static org.mockito.Mockito.when;

import java.util.LinkedList;
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
public class PractitionerValidationTests {

    @Mock
    private PractitionerConfigurationProvider configurationProvider;

    private static List<PractitionerAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP;
    static {
        ALL_REQUIRED_UNDER_35_MAP = new LinkedList<PractitionerAttributeConfiguration>();
        ALL_REQUIRED_UNDER_35_MAP.add(new PractitionerAttributeConfiguration("givenName", "Given Name", "^[a-zA-Z]{1,35}$"));
        ALL_REQUIRED_UNDER_35_MAP.add(new PractitionerAttributeConfiguration("familyName", "Family Name", "^[a-zA-Z]{1,35}$"));
        ALL_REQUIRED_UNDER_35_MAP.add(
                new PractitionerAttributeConfiguration("prefix", "Prefix", "^[a-zA-Z]{1,35}$"));
    }
    
    private static List<PractitionerAttributeConfiguration> PREFIX_NOT_REQUIRED_REGEX_MAP;
    static {
        PREFIX_NOT_REQUIRED_REGEX_MAP = new LinkedList<PractitionerAttributeConfiguration>();
        PREFIX_NOT_REQUIRED_REGEX_MAP.add(new PractitionerAttributeConfiguration("givenName", "Given Name", "^[a-zA-Z]{1,35}$"));
        PREFIX_NOT_REQUIRED_REGEX_MAP.add(new PractitionerAttributeConfiguration("familyName", "Family Name", "^[a-zA-Z]{1,35}$"));
        PREFIX_NOT_REQUIRED_REGEX_MAP.add(
                new PractitionerAttributeConfiguration("prefix", "Prefix", "^[a-zA-Z]{0,35}$"));
    }
    
    private static List<PractitionerAttributeConfiguration> PREFIX_EMPTY_REGEX_MAP;
    static {
        PREFIX_EMPTY_REGEX_MAP = new LinkedList<PractitionerAttributeConfiguration>();
        PREFIX_EMPTY_REGEX_MAP.add(new PractitionerAttributeConfiguration("givenName", "Given Name", "^[a-zA-Z]{1,35}$"));
        PREFIX_EMPTY_REGEX_MAP.add(new PractitionerAttributeConfiguration("familyName", "Family Name", "^[a-zA-Z]{1,35}$"));
        PREFIX_EMPTY_REGEX_MAP.add(new PractitionerAttributeConfiguration("prefix", "Prefix", ""));
    }
    
    private static List<PractitionerAttributeConfiguration> ALL_EMPTY_REGEX_MAP;
    static {
        ALL_EMPTY_REGEX_MAP = new LinkedList<PractitionerAttributeConfiguration>();
        ALL_EMPTY_REGEX_MAP
                .add(new PractitionerAttributeConfiguration("givenName", "Given Name", ""));
        ALL_EMPTY_REGEX_MAP
                .add(new PractitionerAttributeConfiguration("familyName", "Family Name", ""));
        ALL_EMPTY_REGEX_MAP.add(new PractitionerAttributeConfiguration("prefix", "Prefix", ""));
    }

    private static List<PractitionerAttributeConfiguration> INCOMNPLETE_MAP;
    static {
        INCOMNPLETE_MAP = new LinkedList<PractitionerAttributeConfiguration>();
        INCOMNPLETE_MAP
                .add(new PractitionerAttributeConfiguration("givenName", "Given Name", ""));
    }

    private static List<PractitionerAttributeConfiguration> ERROR_MAP;
    static {
        ERROR_MAP = new LinkedList<PractitionerAttributeConfiguration>();
        ERROR_MAP
                .add(new PractitionerAttributeConfiguration("givenName", "Given Name",""));
        ERROR_MAP
                .add(new PractitionerAttributeConfiguration("familyName", "Family Name",""));
        ERROR_MAP
                .add(new PractitionerAttributeConfiguration("wrongname", "Given Name",""));
    }
    
    @ParameterizedTest
    @CsvSource({ ",,,Prefix", ",test,test,Prefix", "test,,test,Given Name", ",test,,Prefix", "null,null,null,Prefix", "test,null,test,Given Name" })
    void TestValidate_WhenFieldsAreEmptyOrNull_ThrowsValidationException(
            @ConvertWith(NullableConverter.class) String prefix, @ConvertWith(NullableConverter.class) String givenName,
            @ConvertWith(NullableConverter.class) String familyName, @ConvertWith(NullableConverter.class) String expectedField) {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
            "Practitioner", ALL_REQUIRED_UNDER_35_MAP));
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);
        var practitionerValidation = new PractitionerValidation(configurationProvider); 

        // Act + Assert
        var result = practitionerValidation.validate(practitioner);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getErrorMessage().contains(expectedField));
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
    void TestValidate_WhenValidPractitioner_ReturnsValidReponse() {
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
            "Practitioner", ALL_REQUIRED_UNDER_35_MAP));
        var practitionerValidation = new PractitionerValidation(configurationProvider);
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);

        // Act
        var result = practitionerValidation.validate(practitioner);
        // Assert
        Assertions.assertTrue(result.isValid());
    }

    @Test
    void TestValidate_WhenPractitionerWithEmtpyPrefix_ReturnsValidReponse() {
        // Arrange
        String prefix = "";
        String givenName = "givenName";
        String familyName = "familyName";
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
        "Practitioner", PREFIX_NOT_REQUIRED_REGEX_MAP));
        var practitionerValidation = new PractitionerValidation(configurationProvider);
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);

        // Act
        var result = practitionerValidation.validate(practitioner);
        // Assert
        Assertions.assertTrue(result.isValid());
    }

    @Test
    void TestValidate_WhenPractitionerWithEmtpyPrefixAndNullRegex_ReturnsValidReponse() {
        // Arrange
        String prefix = "";
        String givenName = "givenName";
        String familyName = "familyName";
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
            "Practitioner", PREFIX_EMPTY_REGEX_MAP));
        var practitionerValidation = new PractitionerValidation(configurationProvider);
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);

        // Act
        var result = practitionerValidation.validate(practitioner);
        // Assert
        Assertions.assertTrue(result.isValid());
    }

    @Test
    void TestValidate_WhenPractitionerWithEmtpyPrefixAndAllNullRegex_ReturnsValidReponse() {
        // Arrange
        String prefix = "";
        String givenName = "givenName";
        String familyName = "familyName";
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person", "Practitioner",
            ALL_EMPTY_REGEX_MAP));
        var practitionerValidation = new PractitionerValidation(configurationProvider);
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);

        // Act
        var result = practitionerValidation.validate(practitioner);
        // Assert
        Assertions.assertTrue(result.isValid());
    }

    @Test
    void TestValidate_WhenPractitionerWithNullPrefix_ReturnsValidReponse() {
        // Arrange
        String prefix = null;
        String givenName = "givenName";
        String familyName = "familyName";
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
        "Practitioner", PREFIX_NOT_REQUIRED_REGEX_MAP));
        var practitionerValidation = new PractitionerValidation(configurationProvider);
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);

        // Act
        var result = practitionerValidation.validate(practitioner);
        // Assert
        Assertions.assertTrue(result.isValid());
    }
}
