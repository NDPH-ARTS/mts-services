package uk.ac.ox.ndph.mts.site_service.converter;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.model.SiteAddress;
import uk.ac.ox.ndph.mts.site_service.model.Site;

/**
 * Implement an EntityConverter for Site. Reverse of {@link OrganizationConverter}.
 */
@Component
public class SiteConverter implements EntityConverter<org.hl7.fhir.r4.model.Organization, Site> {

    EntityConverter<Address, SiteAddress> fromSiteAddressConverter;

    @Autowired
    public void setConverter(final EntityConverter<Address, SiteAddress> fromSiteAddressConverter) {
        this.fromSiteAddressConverter = fromSiteAddressConverter;
    }

    @Override
    public Site convert(final Organization org) {
        Site site = new Site(
                org.getIdElement().getIdPart(),
                org.getName(),
                (org.getAlias().isEmpty()) ? null : org.getAlias().get(0).getValueAsString(),
                findParentSiteId(org),
                org.getImplicitRules()
        );
        site.setAddress(findAddress(org));
        return site;
    }

    private String findParentSiteId(final Organization org) {
        if (org.hasPartOf()) {
            return org.getPartOf().getReferenceElement().getIdPart();
        } else {
            return null;
        }
    }

    private SiteAddress findAddress(final Organization org) {
        if (org.hasAddress()) {
            return fromSiteAddressConverter.convert(org.getAddress().get(0));
        } else {
            return null;
        }
    }

}
