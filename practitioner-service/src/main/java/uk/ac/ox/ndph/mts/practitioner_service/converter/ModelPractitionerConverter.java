package uk.ac.ox.ndph.mts.practitioner_service.converter;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Identifier;
import org.springframework.stereotype.Component;

import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;

import java.util.List;

/**
 * Implement an EntityConverter for Practitioner
 */
@Component
public class ModelPractitionerConverter implements EntityConverter<Practitioner, org.hl7.fhir.r4.model.Practitioner> {

    /**
     * Convert a Practitioner to an hl7 model Practitioner with a random UUID.
     *
     * @param input the practitioner to convert.
     * @return org.hl7.fhir.r4.model.Practitioner
     */
    public org.hl7.fhir.r4.model.Practitioner convert(final Practitioner input) {
        final org.hl7.fhir.r4.model.Practitioner fhirPractitioner = new org.hl7.fhir.r4.model.Practitioner();

        fhirPractitioner.addName()
                .setFamily(input.getFamilyName())
                .addGiven(input.getGivenName())
                .addPrefix(input.getPrefix());

        fhirPractitioner.getIdElement().setValue(input.getId());
        fhirPractitioner.setGender(AdministrativeGender.UNKNOWN);

        if (!StringUtils.isBlank(input.getUserAccountId())) {
            final Identifier identifier = new Identifier();
            identifier.setValue(input.getUserAccountId());
            fhirPractitioner.setIdentifier(List.of(identifier));
        }

        return fhirPractitioner;
    }

}
