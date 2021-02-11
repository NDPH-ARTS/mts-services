package uk.ac.ox.ndph.mts.practitioner_service.converter;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelPractitionerConverterTest {

    private static final String USER_ACCOUNT_ID = "someUserAccountId";
    private ModelPractitionerConverter converter;

    private static Stream<String> getBlankStrings() {
        return Stream.of(null, "", "  ", "\t", "\n");
    }

    @BeforeEach
    public void setup() {
        converter = new ModelPractitionerConverter();
    }

    @Test
    public void convert_ConvertsValidPractitioner() {
        Practitioner practitioner = new Practitioner("42", "prefix", "given", "family");

        var fhirPractitioner = converter.convert(practitioner);
        HumanName practitionerName = fhirPractitioner.getName().get(0);

        assertEquals(practitioner.getId(), fhirPractitioner.getIdElement().toUnqualified().getIdPart());
        assertEquals(practitioner.getPrefix(), practitionerName.getPrefixAsSingleString());
        assertEquals(practitioner.getGivenName(), fhirPractitioner.getName().get(0).getGivenAsSingleString());
        assertEquals(practitioner.getFamilyName(), fhirPractitioner.getName().get(0).getFamily());
    }

    @ParameterizedTest
    @MethodSource("getBlankStrings")
    public void convert_whenModelHasBlankUserAccountId_thenFhirPractitionerHasNoIdentifiers(final String userAccountId) {
        Practitioner practitioner = new Practitioner("42", "prefix", "given", "family");
        practitioner.setUserAccountId(userAccountId);

        // Act
        org.hl7.fhir.r4.model.Practitioner fhirPractitioner = converter.convert(practitioner);

        // Assert
        assertThat(fhirPractitioner.getIdentifier()).isEmpty();
    }

    @Test
    public void convert_whenModelHasUserAccountId_thenFhirPractitionerHasIdentifierWithSameValue() {
        Practitioner practitioner = new Practitioner("42", "prefix", "given", "family");
        practitioner.setUserAccountId(USER_ACCOUNT_ID);

        // Act
        org.hl7.fhir.r4.model.Practitioner fhirPractitioner = converter.convert(practitioner);

        // Assert
        assertThat(fhirPractitioner.getIdentifier()).isNotEmpty().hasSize(1);
        Identifier identifier = fhirPractitioner.getIdentifier().get(0);
        assertThat(identifier.getValue()).isEqualTo(USER_ACCOUNT_ID);
    }

    @Test
    void TestConvertList_WhenCalled_ReturnsException() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> converter.convertList(new ArrayList<>()));
    }
}
