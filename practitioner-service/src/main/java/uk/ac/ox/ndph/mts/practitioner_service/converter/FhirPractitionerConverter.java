package uk.ac.ox.ndph.mts.practitioner_service.converter;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

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
        Objects.requireNonNull(input, "input cannot be null");

        if (input.getName().isEmpty()) {
            throw new IllegalArgumentException("FHIR Practitioner must have a name");
        }

        String userAccountId = input.getIdentifier().stream()
                .filter((id) -> id.getId().equals(Practitioner.USERACCOUNTID_IDENTIFIER_NAME))
                .map(Identifier::getValue)
                .findAny()
                .orElse("");

        HumanName humanName = input.getName().get(0);

        return new Practitioner(input.getIdElement().getIdPart(),
                humanName.getPrefixAsSingleString(),
                humanName.getGivenAsSingleString(),
                humanName.getFamily(),
                userAccountId);
    }

    @Override
    public List<Practitioner> convertList(List<org.hl7.fhir.r4.model.Practitioner> input) {
        return input.stream().map(this::convert).collect(toList());
    }
}
