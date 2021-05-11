package uk.ac.ox.ndph.mts.init_service.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.ox.ndph.mts.init_service.model.SiteAddressDTO;

public class AddressConverterTest {
    private AddressConverter converter;

    @BeforeEach
    void setup() {
        converter = new AddressConverter();
    }

    @Test
    void convert_ConvertsValidAddress() {
        SiteAddressDTO inputAddress = new SiteAddressDTO("address1", "address2", "address3", "address4", "address5", "city", "country", "postcode");
        
        Address address = converter.convert(inputAddress);
        
        List<String> lines = address.getLine().stream().map(s -> s.getValue()).collect(Collectors.toList());
        
        assertEquals(lines.get(0), inputAddress.getAddress1());
        assertEquals(lines.get(1), inputAddress.getAddress2());
        assertEquals(lines.get(2), inputAddress.getAddress3());
        assertEquals(lines.get(3), inputAddress.getAddress4());
        assertEquals(lines.get(4), inputAddress.getAddress5());
        assertEquals(address.getCity(), inputAddress.getCity());
        assertEquals(address.getCountry(), inputAddress.getCountry());
        assertEquals(address.getPostalCode(), inputAddress.getPostcode());
    }
}
