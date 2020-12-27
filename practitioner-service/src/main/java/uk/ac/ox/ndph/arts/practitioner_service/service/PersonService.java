package uk.ac.ox.ndph.arts.practitioner_service.service;

import java.util.UUID;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.arts.practitioner_service.model.Person;
import uk.ac.ox.ndph.arts.practitioner_service.repository.IFhirRepository;
import uk.ac.ox.ndph.arts.practitioner_service.exception.HttpStatusException;
import uk.ac.ox.ndph.arts.practitioner_service.exception.ArgumentException;

/**
* Implement an IEntityService.
* validation is for empty values on all fields.
*/
@Service
public class PersonService implements IEntityService {

    private IFhirRepository fhirRepository;

    @Autowired
    public PersonService(IFhirRepository fhirRepository) {
        this.fhirRepository = fhirRepository;
    }

    public String savePerson(Person person) throws HttpStatusException{
        if (person.getPrefix() == null || person.getPrefix().isEmpty() || person.getGivenName() == null
                || person.getGivenName().isEmpty() || person.getFamilyName() == null
                || person.getFamilyName().isEmpty()) {
            throw new ArgumentException("values cannot be null or empty");
        }
        
        return fhirRepository.savePractitioner(fromPerson(person));
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
