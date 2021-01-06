package uk.ac.ox.ndph.mts.practitioner_service.service;

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
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.repository.FhirRepository;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.practitioner_service.NullableConverter;
import uk.ac.ox.ndph.mts.practitioner_service.configuration.PractitionerAttribute;
import uk.ac.ox.ndph.mts.practitioner_service.configuration.PractitionerConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PractitionerServiceTests {

    @Mock
    private FhirRepository fhirRepository;

    @Mock
    private PractitionerConfigurationProvider configurationProvider;

    @Captor
    ArgumentCaptor<org.hl7.fhir.r4.model.Practitioner> practitionerCaptor;

    private static List<PractitionerAttribute> ALL_REQUIRED_UNDER_35_MAP;
    static {
        ALL_REQUIRED_UNDER_35_MAP = new LinkedList<PractitionerAttribute>();
        ALL_REQUIRED_UNDER_35_MAP.add(new PractitionerAttribute("givenName", "Given Name", "^[a-zA-Z]{1,35}$"));
        ALL_REQUIRED_UNDER_35_MAP.add(new PractitionerAttribute("familyName", "Family Name", "^[a-zA-Z]{1,35}$"));
        ALL_REQUIRED_UNDER_35_MAP.add(
                new PractitionerAttribute("prefix", "Prefix", "^[a-zA-Z]{1,35}$"));
    }
    
    private static List<PractitionerAttribute> PREFIX_NOT_REQUIRED_REGEX_MAP;
    static {
        PREFIX_NOT_REQUIRED_REGEX_MAP = new LinkedList<PractitionerAttribute>();
        PREFIX_NOT_REQUIRED_REGEX_MAP.add(new PractitionerAttribute("givenName", "Given Name", "^[a-zA-Z]{1,35}$"));
        PREFIX_NOT_REQUIRED_REGEX_MAP.add(new PractitionerAttribute("familyName", "Family Name", "^[a-zA-Z]{1,35}$"));
        PREFIX_NOT_REQUIRED_REGEX_MAP.add(
                new PractitionerAttribute("prefix", "Prefix", "^[a-zA-Z]{0,35}$"));
    }
    
    private static List<PractitionerAttribute> PREFIX_EMPTY_REGEX_MAP;
    static {
        PREFIX_EMPTY_REGEX_MAP = new LinkedList<PractitionerAttribute>();
        PREFIX_EMPTY_REGEX_MAP.add(new PractitionerAttribute("givenName", "Given Name", "^[a-zA-Z]{1,35}$"));
        PREFIX_EMPTY_REGEX_MAP.add(new PractitionerAttribute("familyName", "Family Name", "^[a-zA-Z]{1,35}$"));
        PREFIX_EMPTY_REGEX_MAP.add(new PractitionerAttribute("prefix", "Prefix", ""));
    }
    
    private static List<PractitionerAttribute> ALL_EMPTY_REGEX_MAP;
    static {
        ALL_EMPTY_REGEX_MAP = new LinkedList<PractitionerAttribute>();
        ALL_EMPTY_REGEX_MAP
                .add(new PractitionerAttribute("givenName", "Given Name", ""));
        ALL_EMPTY_REGEX_MAP
                .add(new PractitionerAttribute("familyName", "Family Name", ""));
        ALL_EMPTY_REGEX_MAP.add(new PractitionerAttribute("prefix", "Prefix", ""));
    }

    private static List<PractitionerAttribute> INCOMNPLETE_MAP;
    static {
        INCOMNPLETE_MAP = new LinkedList<PractitionerAttribute>();
        INCOMNPLETE_MAP
                .add(new PractitionerAttribute("givenName", "Given Name", ""));
    }

    private static List<PractitionerAttribute> ERROR_MAP;
    static {
        ERROR_MAP = new LinkedList<PractitionerAttribute>();
        ERROR_MAP
                .add(new PractitionerAttribute("givenName", "Given Name",""));
        ERROR_MAP
                .add(new PractitionerAttribute("familyName", "Family Name",""));
        ERROR_MAP
                .add(new PractitionerAttribute("wrongname", "Given Name",""));
    }
    
    @ParameterizedTest
    @CsvSource({ ",,", ",test,test", "test,,test", ",test,", "null,null,null", "test,null,test" })
    void TestSavePractitioner_WhenFieldsAreEmptyOrNull_ThrowsArgumentException(
            @ConvertWith(NullableConverter.class) String prefix, @ConvertWith(NullableConverter.class) String givenName,
            @ConvertWith(NullableConverter.class) String familyName) {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
            "Practitioner", ALL_REQUIRED_UNDER_35_MAP));
        EntityService entityService = new PractitionerService(fhirRepository, configurationProvider);
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);

        // Act + Assert
        Assertions.assertThrows(ValidationException.class, () -> entityService.savePractitioner(practitioner),
                "Expecting empty fields to throw");
    }

    @Test
    void TestPractitionerService_WhenInitWithIncompleteConfig_ThrowsRuntimeException() {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
            "Practitioner", INCOMNPLETE_MAP));

        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new PractitionerService(fhirRepository, configurationProvider),
                "Expecting incomplete configuration to throw");
    }

    @Test
    void TestPractitionerService_WhenInitWithInvalidConfig_ThrowsRuntimeException() {
        // Arrange
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
            "Practitioner", ERROR_MAP));

        // Act + Assert
        Assertions.assertThrows(RuntimeException.class, () -> new PractitionerService(fhirRepository, configurationProvider),
                "Expecting incomplete configuration to throw");
    }

    @Test
    void TestSavePractitioner_WhenSavePractitioner_SavePractitionerToRepository() {
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
            "Practitioner", ALL_REQUIRED_UNDER_35_MAP));
        EntityService entityService = new PractitionerService(fhirRepository, configurationProvider);
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);

        // Act
        entityService.savePractitioner(practitioner);

        // Assert
        Mockito.verify(fhirRepository).savePractitioner(practitionerCaptor.capture());
        org.hl7.fhir.r4.model.Practitioner value = practitionerCaptor.getValue();
        Assertions.assertEquals(prefix, value.getName().get(0).getPrefix().get(0).toString());
        Assertions.assertEquals(givenName, value.getName().get(0).getGiven().get(0).toString());
        Assertions.assertEquals(familyName, value.getName().get(0).getFamily());
    }

    @Test
    void TestSavePractitioner_WhenSavePractitionerWithEmtpyPrefix_SavePractitionerToRepository() {
        // Arrange
        String prefix = "";
        String givenName = "givenName";
        String familyName = "familyName";
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
        "Practitioner", PREFIX_NOT_REQUIRED_REGEX_MAP));
        EntityService entityService = new PractitionerService(fhirRepository, configurationProvider);
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);

        // Act
        entityService.savePractitioner(practitioner);

        // Assert
        Mockito.verify(fhirRepository).savePractitioner(practitionerCaptor.capture());
        org.hl7.fhir.r4.model.Practitioner value = practitionerCaptor.getValue();
        Assertions.assertEquals(prefix, value.getName().get(0).getPrefix().get(0).toString());
        Assertions.assertEquals(givenName, value.getName().get(0).getGiven().get(0).toString());
        Assertions.assertEquals(familyName, value.getName().get(0).getFamily());
    }

    @Test
    void TestSavePractitioner_WhenSavePractitionerWithEmtpyPrefixAndNullRegex_SavePractitionerToRepository() {
        // Arrange
        String prefix = "";
        String givenName = "givenName";
        String familyName = "familyName";
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
            "Practitioner", PREFIX_EMPTY_REGEX_MAP));
        EntityService entityService = new PractitionerService(fhirRepository, configurationProvider);
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);

        // Act
        entityService.savePractitioner(practitioner);

        // Assert
        Mockito.verify(fhirRepository).savePractitioner(practitionerCaptor.capture());
        org.hl7.fhir.r4.model.Practitioner value = practitionerCaptor.getValue();
        Assertions.assertEquals(prefix, value.getName().get(0).getPrefix().get(0).toString());
        Assertions.assertEquals(givenName, value.getName().get(0).getGiven().get(0).toString());
        Assertions.assertEquals(familyName, value.getName().get(0).getFamily());
    }

    @Test
    void TestSavePractitioner_WhenSavePractitionerWithEmtpyPrefixAndAllNullRegex_SavePractitionerToRepository() {
        // Arrange
        String prefix = "";
        String givenName = "givenName";
        String familyName = "familyName";
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person", "Practitioner",
            ALL_EMPTY_REGEX_MAP));
        EntityService entityService = new PractitionerService(fhirRepository, configurationProvider);
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);

        // Act
        entityService.savePractitioner(practitioner);

        // Assert
        Mockito.verify(fhirRepository).savePractitioner(practitionerCaptor.capture());
        org.hl7.fhir.r4.model.Practitioner value = practitionerCaptor.getValue();
        Assertions.assertEquals(prefix, value.getName().get(0).getPrefix().get(0).toString());
        Assertions.assertEquals(givenName, value.getName().get(0).getGiven().get(0).toString());
        Assertions.assertEquals(familyName, value.getName().get(0).getFamily());
    }

    @Test
    void TestSavePractitioner_WhenSavePractitionerWithNullPrefix_SavePractitionerToRepository() {
        // Arrange
        String prefix = null;
        String givenName = "givenName";
        String familyName = "familyName";
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
        "Practitioner", PREFIX_NOT_REQUIRED_REGEX_MAP));
        EntityService entityService = new PractitionerService(fhirRepository, configurationProvider);
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);

        // Act
        entityService.savePractitioner(practitioner);

        // Assert
        Mockito.verify(fhirRepository).savePractitioner(practitionerCaptor.capture());
        org.hl7.fhir.r4.model.Practitioner value = practitionerCaptor.getValue();
        Assertions.assertEquals(prefix, value.getName().get(0).getPrefix().get(0).toString());
        Assertions.assertEquals(givenName, value.getName().get(0).getGiven().get(0).toString());
        Assertions.assertEquals(familyName, value.getName().get(0).getFamily());
    }

    @Test
    void TestSavePractitioner_WhenRepositoryThrows_ThrowsSameException() {
        when(fhirRepository.savePractitioner(Mockito.any(org.hl7.fhir.r4.model.Practitioner.class)))
                .thenThrow(RestException.class);
        when(configurationProvider.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
                "Practitioner", ALL_REQUIRED_UNDER_35_MAP));
        EntityService entityService = new PractitionerService(fhirRepository, configurationProvider);
        Practitioner practitioner = new Practitioner("prefix", "givenName", "familyName");

        // Act + Assert
        Assertions.assertThrows(RestException.class, () -> entityService.savePractitioner(practitioner),
                "Expecting repository error to throw bad gateway");
    }
}
