package uk.ac.ox.ndph.mts.practitioner_service.converter;

import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
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
    public org.hl7.fhir.r4.model.Practitioner convert(Practitioner input) {
        org.hl7.fhir.r4.model.Practitioner fhirPractitioner = new org.hl7.fhir.r4.model.Practitioner();
        fhirPractitioner.addName().setFamily(input.getFamilyName()).addGiven(input.getGivenName())
                .addPrefix(input.getPrefix());
        fhirPractitioner.setGender(AdministrativeGender.UNKNOWN);

        // TODO (archiem) Setting temporary FHIR transaction ID is unnecessary, but not harmful.
        // I think this is the wrong place to be doing this though.
        String id = UUID.randomUUID().toString();
        fhirPractitioner.setId(id);

        return fhirPractitioner;
    }

}
