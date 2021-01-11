package uk.ac.ox.ndph.mts.practitioner_service.service;

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
    private ModelEntityValidation<Practitioner> practitionerValidation;

    // @Captor
    // ArgumentCaptor<Practitioner> practitionerValidationCaptor;

    @Captor
    ArgumentCaptor<Practitioner> practitionerCaptor;


    @Test
    void TestSavePractitioner_WithPractitioner_ValidatesPractitioner() {
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);
        var practitionerService = new PractitionerService(practitionerStore, practitionerValidation);
        when(practitionerValidation.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(true, ""));
        when(practitionerStore.saveEntity(any(Practitioner.class))).thenReturn("123");
        //Act
        practitionerService.savePractitioner(practitioner);

        //Assert
        Mockito.verify(practitionerValidation).validate(practitionerCaptor.capture());
        var value = practitionerCaptor.getValue();
        Assertions.assertEquals(value, practitioner);
    }

    @Test
    void TestSavePractitioner_WhenValidPractitioner_SavesToStore(){
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);
        var practitionerService = new PractitionerService(practitionerStore, practitionerValidation);
        when(practitionerValidation.validate(any(Practitioner.class))).thenReturn(new ValidationResponse(true, ""));
        when(practitionerStore.saveEntity(any(Practitioner.class))).thenReturn("123");
        //Act
        practitionerService.savePractitioner(practitioner);

        //Assert
        Mockito.verify(practitionerStore).saveEntity(practitionerCaptor.capture());
        var value = practitionerCaptor.getValue();
        Assertions.assertEquals(value, practitioner);
    }

    @Test
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
    }
}
