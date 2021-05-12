package uk.ac.ox.ndph.mts.init_service.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import uk.ac.ox.ndph.mts.init_service.model.PractitionerDTO;

class PractitionerConverterTest {

    private static final String USER_ACCOUNT_ID = "someUserAccountId";
    private PractitionerConverter converter;

    private static Stream<String> getBlankStrings() {
        return Stream.of(null, "", "  ", "\t", "\n");
    }

    @BeforeEach
    void setup() {
        converter = new PractitionerConverter();
    }

    @Test
    void convert_ConvertsValidPractitioner() {
        PractitionerDTO practitioner = new PractitionerDTO("42", "prefix", "given", "family", "userAccountId");

        var fhirPractitioner = converter.convert(practitioner);
        HumanName practitionerName = fhirPractitioner.getName().get(0);

        assertEquals(practitioner.getId(), fhirPractitioner.getIdElement().toUnqualified().getIdPart());
        assertEquals(practitioner.getPrefix(), practitionerName.getPrefixAsSingleString());
        assertEquals(practitioner.getGivenName(), fhirPractitioner.getName().get(0).getGivenAsSingleString());
        assertEquals(practitioner.getFamilyName(), fhirPractitioner.getName().get(0).getFamily());
    }

    @ParameterizedTest
    @MethodSource("getBlankStrings")
    void convert_whenModelHasBlankUserAccountId_thenFhirPractitionerHasNoIdentifiers(final String userAccountId) {
        PractitionerDTO practitioner = new PractitionerDTO("42", "prefix", "given", "family", userAccountId);

        org.hl7.fhir.r4.model.Practitioner fhirPractitioner = converter.convert(practitioner);

        assertThat(fhirPractitioner.getIdentifier()).isEmpty();
    }

    @Test
    void convert_whenModelHasUserAccountId_thenFhirPractitionerHasIdentifierWithSameValue() {
        PractitionerDTO practitioner = new PractitionerDTO("42", "prefix", "given", "family", USER_ACCOUNT_ID);

        org.hl7.fhir.r4.model.Practitioner fhirPractitioner = converter.convert(practitioner);

        assertThat(fhirPractitioner.getIdentifier()).isNotEmpty().hasSize(1);
        Identifier identifier = fhirPractitioner.getIdentifier().get(0);
        assertThat(identifier.getValue()).isEqualTo(USER_ACCOUNT_ID);
    }
}
