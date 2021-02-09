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
class PractitionerConverterTests {
    PractitionerConverter converter = new PractitionerConverter();

    @Test
    void TestConvert_WhenValidEntity_ConvertCorrectly(){
        //arrange
        Practitioner practitioner = new Practitioner(
                "prefix", "givenName", "familyName");
        var fhirPractitioner = converter.convert(practitioner);

        //assert
        Assertions.assertAll(
                ()-> Assertions.assertEquals(1, fhirPractitioner.getName().size()),
                ()-> Assertions.assertEquals(practitioner.getFamilyName(), fhirPractitioner.getName().get(0).getFamily()),
                ()-> Assertions.assertEquals(practitioner.getGivenName(), fhirPractitioner.getName().get(0).getGiven().get(0).getValue()),
                ()-> Assertions.assertEquals(practitioner.getPrefix(), fhirPractitioner.getName().get(0).getPrefix().get(0).getValue()),
                ()-> Assertions.assertEquals(Enumerations.AdministrativeGender.UNKNOWN, fhirPractitioner.getGender()),
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
                ()-> Assertions.assertEquals(1, fhirPractitionerList.get(0).getName().size()),
                ()-> Assertions.assertEquals(practitionerList.get(0).getFamilyName(), fhirPractitionerList.get(0).getName().get(0).getFamily()),
                ()-> Assertions.assertEquals(practitionerList.get(0).getGivenName(), fhirPractitionerList.get(0).getName().get(0).getGiven().get(0).getValue()),
                ()-> Assertions.assertEquals(practitionerList.get(0).getPrefix(), fhirPractitionerList.get(0).getName().get(0).getPrefix().get(0).getValue()),
                ()-> Assertions.assertEquals(Enumerations.AdministrativeGender.UNKNOWN, fhirPractitionerList.get(0).getGender()),
                ()-> Assertions.assertNotNull(fhirPractitionerList.get(0).getId()),
                ()-> Assertions.assertEquals(1, fhirPractitionerList.get(1).getName().size()),
                ()-> Assertions.assertEquals(practitionerList.get(1).getFamilyName(), fhirPractitionerList.get(1).getName().get(0).getFamily()),
                ()-> Assertions.assertEquals(practitionerList.get(1).getGivenName(), fhirPractitionerList.get(1).getName().get(0).getGiven().get(0).getValue()),
                ()-> Assertions.assertEquals(practitionerList.get(1).getPrefix(), fhirPractitionerList.get(1).getName().get(0).getPrefix().get(0).getValue()),
                ()-> Assertions.assertEquals(Enumerations.AdministrativeGender.UNKNOWN, fhirPractitionerList.get(1).getGender()),
                ()-> Assertions.assertNotNull(fhirPractitionerList.get(1).getId())
        );
    }
}
