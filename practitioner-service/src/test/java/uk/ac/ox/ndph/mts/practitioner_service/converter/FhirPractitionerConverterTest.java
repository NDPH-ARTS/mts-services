package uk.ac.ox.ndph.mts.practitioner_service.converter;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Practitioner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FhirPractitionerConverterTest {

    private FhirPractitionerConverter converter;

    @BeforeEach
    public void setup() {
        converter = new FhirPractitionerConverter();
    }

    @Test
    public void convert_ConvertsValidPractitioner() {
        Practitioner practitioner = new Practitioner();
        HumanName humanName = new HumanName();
        humanName.addPrefix("Mr");
        humanName.addGiven("Given");
        humanName.setFamily("Family");

        practitioner.addName(humanName);
        practitioner.setIdBase("base id");

        var modelPractitioner = converter.convert(practitioner);
        HumanName practitionerName = practitioner.getName().get(0);

        assertEquals(practitioner.getIdElement().toUnqualified().getIdPart(), modelPractitioner.getId());
        assertEquals(practitionerName.getPrefixAsSingleString(), modelPractitioner.getPrefix());
        assertEquals(practitioner.getName().get(0).getGivenAsSingleString(), modelPractitioner.getGivenName());
        assertEquals(practitioner.getName().get(0).getFamily(), modelPractitioner.getFamilyName());
    }

    @Test
    void TestConvertList_WhenCalled_ReturnsException() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> converter.convertList(new ArrayList<>()));
    }
}
