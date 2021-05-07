package uk.ac.ox.ndph.mts.init_service.converter;


import org.hl7.fhir.r4.model.Address;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.init_service.model.SiteAddress;

/**
 * Implement an EntityConverter for {@link SiteAddress} to {@link Address}.
 */
@Component
public class AddressConverter implements EntityConverter<SiteAddress, Address> {

    /**
     * Convert a uk.ac.ox.ndph.mts.site_service.model.SiteAddress to an hl7 model Address
     *
     * @param input the uk.ac.ox.ndph.mts.site_service.model.SiteAddress to convert.
     * @return org.hl7.fhir.r4.model.Address
     */
    public Address convert(SiteAddress input) {
        Address fhirAddress = new Address();

        if (input.getAddress1() != null) {
            fhirAddress.addLine(input.getAddress1());
        }
        if (input.getAddress2() != null) {
            fhirAddress.addLine(input.getAddress2());
        }
        if (input.getAddress3() != null) {
            fhirAddress.addLine(input.getAddress3());
        }
        if (input.getAddress4() != null) {
            fhirAddress.addLine(input.getAddress4());
        }
        if (input.getAddress5() != null) {
            fhirAddress.addLine(input.getAddress5());
        }
        if (input.getCity() != null) {
            fhirAddress.setCity(input.getCity());
        }
        if (input.getCountry() != null) {
            fhirAddress.setCountry(input.getCountry());
        }
        if (input.getPostcode() != null) {
            fhirAddress.setPostalCode(input.getPostcode());
        }

        return fhirAddress;
    }
}
