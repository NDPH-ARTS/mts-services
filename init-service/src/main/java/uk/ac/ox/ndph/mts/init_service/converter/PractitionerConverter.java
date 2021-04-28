package uk.ac.ox.ndph.mts.init_service.converter;

import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import uk.ac.ox.ndph.mts.init_service.model.PractitionerDTO;

/**
 * Implement an EntityConverter for Practitioner
 */
@Component
public class PractitionerConverter implements EntityConverter<PractitionerDTO, org.hl7.fhir.r4.model.Practitioner> {
    public static final String USERACCOUNTID_IDENTIFIER_NAME = "ID_PROVIDER_USER_ACCOUNT_IDENTIFIER";

    /**
     * Convert a Practitioner to an hl7 model Practitioner with a random UUID.
     *
     * @param input the practitioner to convert.
     * @return org.hl7.fhir.r4.model.Practitioner
     */
    public org.hl7.fhir.r4.model.Practitioner convert(final PractitionerDTO input) {
        final org.hl7.fhir.r4.model.Practitioner fhirPractitioner = new org.hl7.fhir.r4.model.Practitioner();

        fhirPractitioner.addName()
                .setFamily(input.getFamilyName())
                .addGiven(input.getGivenName())
                .addPrefix(input.getPrefix());

        fhirPractitioner.getIdElement().setValue(input.getId());
        fhirPractitioner.setGender(AdministrativeGender.UNKNOWN);

        if (StringUtils.hasText(input.getUserAccount())) {
            final Identifier identifier = new Identifier();
            identifier.setUse(IdentifierUse.OFFICIAL);
            identifier.setId(USERACCOUNTID_IDENTIFIER_NAME);
            identifier.setValue(input.getUserAccount());            
            fhirPractitioner.addIdentifier(identifier);
        }

        return fhirPractitioner;
    }

}
