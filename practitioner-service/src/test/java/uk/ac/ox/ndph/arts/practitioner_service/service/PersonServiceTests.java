package uk.ac.ox.ndph.arts.practitioner_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.converter.ConvertWith;
import org.hl7.fhir.r4.model.Practitioner;

import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.Assertions;
import uk.ac.ox.ndph.arts.practitioner_service.model.Person;
import uk.ac.ox.ndph.arts.practitioner_service.service.IEntityService;
import uk.ac.ox.ndph.arts.practitioner_service.repository.IFhirRepository;
import uk.ac.ox.ndph.arts.practitioner_service.exception.HttpStatusException;
import uk.ac.ox.ndph.arts.practitioner_service.exception.RestException;
import uk.ac.ox.ndph.arts.practitioner_service.exception.ArgumentException;

@SpringBootTest
public class PersonServiceTests {

    @ParameterizedTest
    @CsvSource({",,", ",test,test", "test,,test", ",test,", "null,null,null", "test,null,test"})
    void TestSavePerson_WhenFieldsAreEmptyOrNull_ThrowsArgumentException(
        @ConvertWith(NullableConverter.class)String prefix, 
        @ConvertWith(NullableConverter.class)String givenName, 
        @ConvertWith(NullableConverter.class)String familyName) {
        // Arrange
        IEntityService personService = new PersonService(Mockito.mock(IFhirRepository.class));
        Person person = new Person(prefix, givenName, familyName);
        
        // Act + Assert
        Assertions.assertThrows(ArgumentException.class, () -> {
            personService.savePerson(person);
          }, "Expecting empty fields to throw");
    } 

    @Test
    void TestSavePerson_WhenSavePerson_SavePractitionerToRepository(){
        // Arrange
        String prefix = "prefix";
        String givenName = "givenName";
        String familyName = "familyName";
        IFhirRepository mockFhirRepository = Mockito.mock(IFhirRepository.class);
        ArgumentCaptor<Practitioner> argumentCaptor = ArgumentCaptor.forClass(Practitioner.class);
        IEntityService personService = new PersonService(mockFhirRepository);
        Person person = new Person(prefix, givenName, familyName);
        
        // Act
        personService.savePerson(person);

        // Assert
        Mockito.verify(mockFhirRepository).savePractitioner(argumentCaptor.capture());
        Practitioner value = argumentCaptor.getValue();
        Assertions.assertEquals(prefix, value.getName().get(0).getPrefix().get(0).toString());
        Assertions.assertEquals(givenName, value.getName().get(0).getGiven().get(0).toString());
        Assertions.assertEquals(familyName, value.getName().get(0).getFamily().toString());
    }

    @Test
    void TestSavePerson_WhenRepositoryThrows_ThrowsSameException(){
        IFhirRepository mockFhirRepository = Mockito.mock(IFhirRepository.class);
        when(mockFhirRepository.savePractitioner(Mockito.any(Practitioner.class)))
            .thenThrow(RestException.class);
  
        IEntityService personService = new PersonService(mockFhirRepository);
        Person person = new Person("prefix", "givenName", "familyName");
        
        // Act + Assert
        Assertions.assertThrows(RestException.class, () -> {
            personService.savePerson(person);
          }, "Expecting empty fields to throw");
    }
}
