package uk.ac.ox.ndph.mts.practitioner_service.converter;

import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.springframework.stereotype.Component;

/**
 * Implement an EntityConverter for Practitioner
 */
@Component
public class PractitionerConverter implements EntityConverter<Practitioner, org.hl7.fhir.r4.model.Practitioner> {

    /**
     * Convert a Practitioner to an hl7 model Practitioner with a random UUID.
     * @param input the practitioner to convert.
     * @return org.hl7.fhir.r4.model.Practitioner
     */
    @Override
    public org.hl7.fhir.r4.model.Practitioner convert(Practitioner input) {
        org.hl7.fhir.r4.model.Practitioner fhirPractitioner = new org.hl7.fhir.r4.model.Practitioner();
        fhirPractitioner.addName().setFamily(input.getFamilyName()).addGiven(input.getGivenName())
                .addPrefix(input.getPrefix());
        fhirPractitioner.setGender(AdministrativeGender.UNKNOWN);
        String id = UUID.randomUUID().toString();
        fhirPractitioner.setId(id);
        return fhirPractitioner;
    }

    @Override
    public List<org.hl7.fhir.r4.model.Practitioner> convertList(List<Practitioner> input) {
        List<org.hl7.fhir.r4.model.Practitioner> practitionerList = new ArrayList<>();
        for (var practitioner: input) {
            practitionerList.add(convert(practitioner));
        }
        return  practitionerList;
    }
}
