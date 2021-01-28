package uk.ac.ox.ndph.mts.practitioner_service.converter;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Practitioner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class PractitionerConverterTest {

    @Mock
    uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner person;

    @Test
    void test() {
        // Arrange
        PractitionerConverter sut = new PractitionerConverter();

        // Act
        Practitioner fhirPractitioner = sut.convert(person);

        // Assert
        List<Identifier> identifiers = fhirPractitioner.getIdentifier();
        assertThat(identifiers).describedAs("Practitioner identifiers").isNotEmpty();

        Stream<String> systems = identifiers.stream().map(Identifier::getSystem);
        assertThat(systems).describedAs("Identifier systems").containsOnlyOnce("urn:personId");

        Optional<Identifier> personId = identifiers.stream().filter(id -> "urn:personId".equals(id.getSystem())).findFirst();
        assertThat(personId).isPresent().hasValueSatisfying(p ->
                assertThat(p.getValue()).isNotBlank());
    }
}
