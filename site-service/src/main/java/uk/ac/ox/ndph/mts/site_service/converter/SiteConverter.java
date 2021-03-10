package uk.ac.ox.ndph.mts.site_service.converter;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.model.Address;
import uk.ac.ox.ndph.mts.site_service.model.Site;

import java.util.List;

/**
 * Implement an EntityConverter for Site. Reverse of {@link OrganizationConverter}.
 */
@Component
public class SiteConverter implements EntityConverter<org.hl7.fhir.r4.model.Organization, Site> {

    @Override
    public Site convert(final Organization org) {
        return new Site(
                org.getIdElement().getIdPart(),
                org.getName(),
                (org.getAlias().isEmpty()) ? null : org.getAlias().get(0).getValueAsString(),
                findParentSiteId(org),
                org.getImplicitRules(),
                findAddress(org)
        );
    }

    private Address findAddress(Organization org) {
        if (org.hasAddress()) {
            List<StringType> line = org.getAddress().get(0).getLine();
            return new Address(!line.isEmpty() ? line.get(0).getValue() : "",
                    !line.isEmpty() && line.size() > 1 ? line.get(1).getValue() : "",
                    !line.isEmpty() && line.size() > 2 ? line.get(2).getValue() : "",
                    !line.isEmpty() && line.size() > 3 ? line.get(3).getValue() : "",
                    !line.isEmpty() && line.size() > 4 ? line.get(4).getValue() : "",
                    org.getAddress().get(0).getCity() != null ? org.getAddress().get(0).getCity() : "",
                    org.getAddress().get(0).getCountry() != null ? org.getAddress().get(0).getCountry() : "",
                    org.getAddress().get(0).getPostalCode() != null ? org.getAddress().get(0).getPostalCode() : "");
        } else {
            return null;
        }
    }

    private String findParentSiteId(final Organization org) {
        if (org.hasPartOf()) {
            return org.getPartOf().getReferenceElement().getIdPart();
        } else {
            return null;
        }
    }

}
