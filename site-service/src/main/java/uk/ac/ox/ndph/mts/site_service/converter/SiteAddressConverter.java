package uk.ac.ox.ndph.mts.site_service.converter;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.model.SiteAddress;

import java.util.List;

/**
 * Implement an EntityConverter for SiteAddress. Reverse of {@link AddressConverter}.
 */
@Component
public class SiteAddressConverter implements EntityConverter<Address, SiteAddress> {

    @Override
    public SiteAddress convert(final Address org) {

        final List<StringType> lines = org.getLine();

        return new SiteAddress(
                !lines.isEmpty() ? lines.get(0).getValue() : "",
                !lines.isEmpty() && lines.size() > 1 ? lines.get(1).getValue() : "",
                !lines.isEmpty() && lines.size() > 2 ? lines.get(2).getValue() : "",
                !lines.isEmpty() && lines.size() > 3 ? lines.get(3).getValue() : "",
                !lines.isEmpty() && lines.size() > 4 ? lines.get(4).getValue() : "",
                org.getCity() != null ? org.getCity() : "",
                org.getCountry() != null ? org.getCountry() : "",
                org.getPostalCode() != null ? org.getPostalCode() : ""
        );
    }

}
