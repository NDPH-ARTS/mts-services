package uk.ac.ox.ndph.mts.practitioner_service.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hl7.fhir.r4.model.HumanName;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;

public class ModelPractitionerConverterTest {

    private ModelPractitionerConverter converter;

    @Before
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
}