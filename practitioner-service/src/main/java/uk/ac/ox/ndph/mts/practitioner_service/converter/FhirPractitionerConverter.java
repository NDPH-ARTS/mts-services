package uk.ac.ox.ndph.mts.practitioner_service.converter;

import org.hl7.fhir.r4.model.HumanName;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;

import java.util.List;
import java.util.Optional;

/**
 * Implement an EntityConverter for Practitioner
 */
@Component
public class FhirPractitionerConverter implements EntityConverter<org.hl7.fhir.r4.model.Practitioner, Practitioner> {
    /**
     * Convert an hl7 Practitioner to a model Practitioner.
     *
     * @param input the practitioner to convert.
     * @return uk.ac.ox.ndph.mts.practioner_service.model.Practitioner
     */
    public Practitioner convert(org.hl7.fhir.r4.model.Practitioner input) {
        Optional<HumanName> practitionerName = input.getName().stream().findFirst();
        if (practitionerName.isPresent()) {
            HumanName humanName = practitionerName.get();
            Practitioner practitioner =
                    new Practitioner(input.getIdElement().toUnqualified().getIdPart(),
                            humanName.getPrefixAsSingleString(),
                            humanName.getGivenAsSingleString(),
                            humanName.getFamily());

            return practitioner;
        } else {
            return new Practitioner(input.getId(), "", "", "");
        }
    }

    @Override
    public List<Practitioner> convertList(List<org.hl7.fhir.r4.model.Practitioner> input) {
        throw new UnsupportedOperationException();
    }
}
