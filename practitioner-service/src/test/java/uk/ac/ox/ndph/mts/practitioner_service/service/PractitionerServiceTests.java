package uk.ac.ox.ndph.mts.practitioner_service.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
<<<<<<< HEAD

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
=======
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
>>>>>>> main
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.ac.ox.ndph.mts.practitioner_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.ValidationResponse;
import uk.ac.ox.ndph.mts.practitioner_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.practitioner_service.validation.ModelEntityValidation;

@ExtendWith(MockitoExtension.class)
class PractitionerServiceTests {
    
    @Mock
    private EntityStore<Practitioner> practitionerStore;

    @Mock
<<<<<<< HEAD
    private PractitionerConfigurationProvider configurationProvider;

=======
    private ModelEntityValidation<Practitioner> practitionerValidation;
    
>>>>>>> main
    @Captor
    ArgumentCaptor<Practitioner> practitionerCaptor;

<<<<<<< HEAD
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
=======

    @Test
    void TestSavePractitioner_WithPractitioner_ValidatesPractitioner() {
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
>>>>>>> main
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);
        var practitionerService = new PractitionerService(practitionerStore, practitionerValidation);
        when(practitionerValidation.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(true, ""));
        when(practitionerStore.saveEntity(any(Practitioner.class))).thenReturn("123");
        //Act
        practitionerService.savePractitioner(practitioner);

<<<<<<< HEAD
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
=======
        //Assert
        Mockito.verify(practitionerValidation).validate(practitionerCaptor.capture());
        var value = practitionerCaptor.getValue();
        assertThat(practitioner, equalTo(value));
>>>>>>> main
    }

    @Test
    void TestSavePractitioner_WhenValidPractitioner_SavesToStore(){
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
<<<<<<< HEAD
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
=======
>>>>>>> main
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);
        var practitionerService = new PractitionerService(practitionerStore, practitionerValidation);
        when(practitionerValidation.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(true, ""));
        when(practitionerStore.saveEntity(any(Practitioner.class))).thenReturn("123");
        //Act
        practitionerService.savePractitioner(practitioner);

        //Assert
        Mockito.verify(practitionerStore).saveEntity(practitionerCaptor.capture());
        var value = practitionerCaptor.getValue();
        assertThat(practitioner, equalTo(value));
    }

    @Test
<<<<<<< HEAD
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
=======
    void TestSavePractitioner_WhenInvalidPractitioner_ThrowsValidationException(){
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);
        var practitionerService = new PractitionerService(practitionerStore, practitionerValidation);
        when(practitionerValidation.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(false, "prefix"));
        //Act + Assert
        Assertions.assertThrows(ValidationException.class, () -> practitionerService.savePractitioner(practitioner),
                "Expecting save to throw validation exception");
    }

    @Test
    void TestSavePractitioner_WhenInvalidPractitioner_DoesntSavesToStore(){
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);
        var practitionerService = new PractitionerService(practitionerStore, practitionerValidation);
        when(practitionerValidation.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(false, "prefix"));
        //Act + Assert
        Assertions.assertThrows(ValidationException.class, () -> practitionerService.savePractitioner(practitioner),
                "Expecting save to throw validation exception");
        Mockito.verify(practitionerStore, Mockito.times(0)).saveEntity(any(Practitioner.class));
    }

    @Test
    void TestPractitionerService_WhenNullValues_ThrowsInitialisationError(){
        // Arrange + Act + Assert
        Assertions.assertThrows(InitialisationError.class, () -> new PractitionerService(null, practitionerValidation),
                "null store should throw");
        Assertions.assertThrows(InitialisationError.class, () -> new PractitionerService(practitionerStore, null),
                "null validation should throw");
>>>>>>> main
    }
}
