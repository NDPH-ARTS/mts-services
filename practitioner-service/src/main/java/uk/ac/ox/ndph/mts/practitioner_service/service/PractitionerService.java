package uk.ac.ox.ndph.mts.practitioner_service.service;

import java.util.UUID;

import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.repository.FhirRepository;
import uk.ac.ox.ndph.mts.practitioner_service.exception.ArgumentException;

/**
 * Implement an EntityService interface.
 * validation is for empty values on all fields.
 */
@Service
public class PractitionerService implements EntityService {
    private static final String FIELD_NAME_PREFIX = "prefix";
    private static final String FIELD_NAME_GIVEN_NAME = "given name";
    private static final String FIELD_NAME_FAMILY_NAME = "family name";
    private static final String ERROR_MESSAGE = "value of argument %s cannot be empty";

    private final FhirRepository fhirRepository;

    /**
     *
     * @param fhirRepository
     */
    @Autowired
    public PractitionerService(FhirRepository fhirRepository) {
        this.fhirRepository = fhirRepository;
    }

    /**
     *
     * @param practitioner the Practitioner to save.
     * @return A new Practitioner
     */
    public String savePractitioner(Practitioner practitioner) {

        validateArgument(practitioner.getPrefix(), FIELD_NAME_PREFIX);
        validateArgument(practitioner.getGivenName(), FIELD_NAME_GIVEN_NAME);
        validateArgument(practitioner.getFamilyName(), FIELD_NAME_FAMILY_NAME);

        return fhirRepository.savePractitioner(toFhirPractitioner(practitioner));
    }

    private void validateArgument(String value, String argumentName) {
        if (value == null || value.isBlank()) {
            throw new ArgumentException(String.format(ERROR_MESSAGE, argumentName));
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
