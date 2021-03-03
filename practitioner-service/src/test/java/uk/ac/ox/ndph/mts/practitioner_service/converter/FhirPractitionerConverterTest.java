package uk.ac.ox.ndph.mts.practitioner_service.converter;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Practitioner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FhirPractitionerConverterTest {

    private FhirPractitionerConverter converter;

    @BeforeEach
    void setup() {
        converter = new FhirPractitionerConverter();
    }

    @Test
    void convert_ConvertsValidPractitioner() {
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
    void TestConvert_WithInvalidFhirPractitioner_ThrowsAnException() {
        Assertions.assertThrows(NullPointerException.class, () -> converter.convert(null));

        Practitioner practitioner = new Practitioner();
        Assertions.assertThrows(IllegalArgumentException.class, () -> converter.convert(practitioner));
    }

    @Test
    void TestConvertList_ConvertsList() {
        Practitioner one = new Practitioner();
        HumanName nameOne = new HumanName();
        nameOne.setFamily("One");
        one.addName(nameOne);
        
        Practitioner two = new Practitioner();
        HumanName nameTwo = new HumanName();
        nameTwo.setFamily("Two");
        two.addName(nameTwo);
        
        var converted = converter.convertList(Arrays.asList(one, two));
        
        assertEquals(2, converted.size());
        assertTrue(converted.stream().anyMatch(p -> p.getFamilyName().equals("One")));
        assertTrue(converted.stream().anyMatch(p -> p.getFamilyName().equals("Two")));
    }
}
