package uk.ac.ox.ndph.mts.site_service.converter;

import org.hl7.fhir.r4.model.Organization;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.model.Site;

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
                findParentSiteId(org)
        );
    }

    private String findParentSiteId(final Organization org) {
        if (org.hasPartOf()) {
            return org.getPartOf().getReferenceElement().getIdPart();
        } else {
            return null;
        }
    }

}
