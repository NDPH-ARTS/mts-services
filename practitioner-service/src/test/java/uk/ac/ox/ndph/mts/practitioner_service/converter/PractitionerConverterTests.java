package uk.ac.ox.ndph.mts.practitioner_service.converter;

import org.checkerframework.checker.units.qual.A;
import org.hl7.fhir.r4.model.Linkage;
import org.junit.jupiter.api.Assertions;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import org.hl7.fhir.r4.model.Enumerations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class PractitionerConverterTests {
    PractitionerConverter converter = new PractitionerConverter();

    @Test
    void TestConvert_WhenValidEntity_ConvertCorrectly(){
        //arrange
        Practitioner practitioner = new Practitioner(
                "prefix", "givenName", "familyName");
        var fhirPractitioner = converter.convert(practitioner);

        //assert
        Assertions.assertAll(
                ()-> Assertions.assertEquals(fhirPractitioner.getName().size(), 1),
                ()-> Assertions.assertEquals(fhirPractitioner.getName().get(0).getFamily(), practitioner.getFamilyName()),
                ()-> Assertions.assertEquals(fhirPractitioner.getName().get(0).getGiven().get(0).getValue(), practitioner.getGivenName()),
                ()-> Assertions.assertEquals(fhirPractitioner.getName().get(0).getPrefix().get(0).getValue(), practitioner.getPrefix()),
                ()-> Assertions.assertEquals(fhirPractitioner.getGender(), Enumerations.AdministrativeGender.UNKNOWN),
                ()-> Assertions.assertNotNull(fhirPractitioner.getId())
        );
    }

    @Test
    void TestConvertList_WhenValidEntityList_ConvertListCorrectly(){
        //arrange
        List<Practitioner> practitionerList = List.of(
                new Practitioner("prefix1", "givenName1", "familyName1"),
                new Practitioner("prefix2", "givenName2", "familyName2")
        );

        var fhirPractitionerList = converter.convertList(practitionerList);

        //assert
        Assertions.assertAll(
                ()-> Assertions.assertEquals(fhirPractitionerList.get(0).getName().size(), 1),
                ()-> Assertions.assertEquals(fhirPractitionerList.get(0).getName().get(0).getFamily(), practitionerList.get(0).getFamilyName()),
                ()-> Assertions.assertEquals(fhirPractitionerList.get(0).getName().get(0).getGiven().get(0).getValue(), practitionerList.get(0).getGivenName()),
                ()-> Assertions.assertEquals(fhirPractitionerList.get(0).getName().get(0).getPrefix().get(0).getValue(), practitionerList.get(0).getPrefix()),
                ()-> Assertions.assertEquals(fhirPractitionerList.get(0).getGender(), Enumerations.AdministrativeGender.UNKNOWN),
                ()-> Assertions.assertNotNull(fhirPractitionerList.get(0).getId()),
                ()-> Assertions.assertEquals(fhirPractitionerList.get(1).getName().size(), 1),
                ()-> Assertions.assertEquals(fhirPractitionerList.get(1).getName().get(0).getFamily(), practitionerList.get(1).getFamilyName()),
                ()-> Assertions.assertEquals(fhirPractitionerList.get(1).getName().get(0).getGiven().get(0).getValue(), practitionerList.get(1).getGivenName()),
                ()-> Assertions.assertEquals(fhirPractitionerList.get(1).getName().get(0).getPrefix().get(0).getValue(), practitionerList.get(1).getPrefix()),
                ()-> Assertions.assertEquals(fhirPractitionerList.get(1).getGender(), Enumerations.AdministrativeGender.UNKNOWN),
                ()-> Assertions.assertNotNull(fhirPractitionerList.get(1).getId())
        );
    }
}
