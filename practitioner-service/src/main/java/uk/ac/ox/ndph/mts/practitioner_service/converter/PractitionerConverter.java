package uk.ac.ox.ndph.mts.practitioner_service.converter;

import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;

@Component
public class PractitionerConverter implements EntityConverter<Practitioner, org.hl7.fhir.r4.model.Practitioner> {

    public org.hl7.fhir.r4.model.Practitioner convert(Practitioner input) {
        org.hl7.fhir.r4.model.Practitioner fhirPractitioner = new org.hl7.fhir.r4.model.Practitioner();
        fhirPractitioner.addName()
                .setFamily(input.getFamilyName())
                .addGiven(input.getGivenName())
                .addPrefix(input.getPrefix());
        fhirPractitioner.setGender(AdministrativeGender.UNKNOWN);

        return fhirPractitioner;
    }

}
