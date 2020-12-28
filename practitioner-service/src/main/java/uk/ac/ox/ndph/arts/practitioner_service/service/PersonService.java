package uk.ac.ox.ndph.arts.practitioner_service.service;

import java.util.UUID;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.arts.practitioner_service.model.Person;
import uk.ac.ox.ndph.arts.practitioner_service.repository.FhirRepository;
import uk.ac.ox.ndph.arts.practitioner_service.exception.HttpStatusException;
import uk.ac.ox.ndph.arts.practitioner_service.exception.ArgumentException;

/**
* Implement an EntityService interface.
* validation is for empty values on all fields.
*/
@Service
public class PersonService implements EntityService {

    private FhirRepository fhirRepository;

    @Autowired
    public PersonService(FhirRepository fhirRepository) {
        this.fhirRepository = fhirRepository;
    }

    public String savePerson(Person person) throws HttpStatusException{
        validateArgument(person.getPrefix(), "prefix");
        validateArgument(person.getGivenName(), "given name");
        validateArgument(person.getFamilyName(), "family name");
        
        return fhirRepository.savePractitioner(fromPerson(person));
    }

    private void validateArgument(String value, String argumentName) throws HttpStatusException{
        if (value == null || value.isBlank()){
            throw new ArgumentException(String.format("value of argument %s cannot be empty", argumentName));
        }
    }

    private Practitioner fromPerson(Person person) {
        Practitioner practitioner = new Practitioner();
        practitioner.addName().setFamily(person.getFamilyName()).addGiven(person.getGivenName())
                .addPrefix(person.getPrefix());
        practitioner.setGender(AdministrativeGender.UNKNOWN);
        String id = UUID.randomUUID().toString();
        practitioner.setId(id);
        return practitioner;
    }
}
