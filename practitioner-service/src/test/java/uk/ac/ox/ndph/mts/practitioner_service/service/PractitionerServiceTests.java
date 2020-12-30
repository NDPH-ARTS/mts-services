package uk.ac.ox.ndph.mts.practitioner_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.converter.ConvertWith;

import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.Captor;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.Assertions;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.repository.FhirRepository;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.mts.practitioner_service.NullableConverter;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ArgumentException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PractitionerServiceTests {

    @Mock
    private FhirRepository fhirRepository;

    @Captor
    ArgumentCaptor<org.hl7.fhir.r4.model.Practitioner> practitionerCaptor;

    @ParameterizedTest
    @CsvSource({",,", ",test,test", "test,,test", ",test,", "null,null,null", "test,null,test"})
    void TestSavePractitioner_WhenFieldsAreEmptyOrNull_ThrowsArgumentException(
        @ConvertWith(NullableConverter.class)String prefix, 
        @ConvertWith(NullableConverter.class)String givenName, 
        @ConvertWith(NullableConverter.class)String familyName) {
        // Arrange
        EntityService entityService = new PractitionerService(fhirRepository);
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);
        
        // Act + Assert
        Assertions.assertThrows(ArgumentException.class, () -> {
            entityService.savePractitioner(practitioner);
          }, "Expecting empty fields to throw");
    } 

    @Test
    void TestSavePractitioner_WhenSavePractitioner_SavePractitionerToRepository(){
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
        EntityService entityService = new PractitionerService(fhirRepository);
        Practitioner practitioner = new Practitioner(prefix, givenName, familyName);
        
        // Act
        entityService.savePractitioner(practitioner);

        // Assert
        Mockito.verify(fhirRepository).savePractitioner(practitionerCaptor.capture());
        org.hl7.fhir.r4.model.Practitioner value = practitionerCaptor.getValue();
        Assertions.assertEquals(prefix, value.getName().get(0).getPrefix().get(0).toString());
        Assertions.assertEquals(givenName, value.getName().get(0).getGiven().get(0).toString());
        Assertions.assertEquals(familyName, value.getName().get(0).getFamily().toString());
    }

    @Test
    void TestSavePractitioner_WhenRepositoryThrows_ThrowsSameException(){
        when(fhirRepository.savePractitioner(Mockito.any(org.hl7.fhir.r4.model.Practitioner.class)))
            .thenThrow(RestException.class);
  
        EntityService entityService = new PractitionerService(fhirRepository);
        Practitioner practitioner = new Practitioner("prefix", "givenName", "familyName");
        
        // Act + Assert
        Assertions.assertThrows(RestException.class, () -> {
            entityService.savePractitioner(practitioner);
          }, "Expecting empty fields to throw");
    }
}
