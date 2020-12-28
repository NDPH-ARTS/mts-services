package uk.ac.ox.ndph.arts.practitioner_service.service;

import java.util.UUID;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.arts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.arts.practitioner_service.repository.FhirRepository;
import uk.ac.ox.ndph.arts.practitioner_service.exception.ArgumentException;

/**
* Implement an EntityService interface.
* validation is for empty values on all fields.
*/
@Service
public class PractitionerService implements EntityService {

    private FhirRepository fhirRepository;

    @Autowired
    public PractitionerService(FhirRepository fhirRepository) {
        this.fhirRepository = fhirRepository;
    }

    public String savePractitioner(Practitioner practitioner) {
        validateArgument(practitioner.getPrefix(), "prefix");
        validateArgument(practitioner.getGivenName(), "given name");
        validateArgument(practitioner.getFamilyName(), "family name");
        
        return fhirRepository.savePractitioner(toFhirPractitioner(practitioner));
    }

    private void validateArgument(String value, String argumentName){
        if (value == null || value.isBlank()){
            throw new ArgumentException(String.format("value of argument %s cannot be empty", argumentName));
        }
    }

    private org.hl7.fhir.r4.model.Practitioner toFhirPractitioner(Practitioner practitioner) {
        org.hl7.fhir.r4.model.Practitioner fhirPractitioner = new org.hl7.fhir.r4.model.Practitioner();
        fhirPractitioner.addName().setFamily(practitioner.getFamilyName()).addGiven(practitioner.getGivenName())
                .addPrefix(practitioner.getPrefix());
                fhirPractitioner.setGender(AdministrativeGender.UNKNOWN);
        String id = UUID.randomUUID().toString();
        fhirPractitioner.setId(id);
        return fhirPractitioner;
    }
}
