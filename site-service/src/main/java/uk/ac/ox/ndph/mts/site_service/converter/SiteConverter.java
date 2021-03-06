package uk.ac.ox.ndph.mts.site_service.converter;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.SiteAddress;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implement an EntityConverter for Site. Reverse of {@link OrganizationConverter}.
 */
@Component
public class SiteConverter implements EntityConverter<org.hl7.fhir.r4.model.Organization, Site> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteConverter.class);
    EntityConverter<Address, SiteAddress> fromSiteAddressConverter;

    @Autowired
    public void setConverter(final EntityConverter<Address, SiteAddress> fromSiteAddressConverter) {
        this.fromSiteAddressConverter = fromSiteAddressConverter;
    }

    @Override
    public Site convert(final Organization org) {
        LOGGER.info("About to convert org to site");
        Site site = new Site(
                org.getIdElement().getIdPart(),
                org.getName(),
                (org.getAlias().isEmpty()) ? null : org.getAlias().get(0).getValueAsString(),
                findParentSiteId(org),
                org.getImplicitRules(),
                org.getText().getDiv().allText(),
                LocalDateTime.ofInstant(org.getMeta().getLastUpdated().toInstant(), ZoneId.systemDefault()),
                org.getActive() ? Status.ACTIVE.getValue() : Status.INACTIVE.getValue());
        site.setAddress(findAddress(org));
        site.setExtensions(findExtentions(org));
        return site;
    }

    private Map<String, String> findExtentions(Organization org) {
        if (org.hasExtension()) {
            return org.getExtension().stream().
                    collect(Collectors.toMap(Extension::getUrl, extension -> extension.getValue().toString()));
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

    private SiteAddress findAddress(final Organization org) {
        if (org.hasAddress()) {
            return fromSiteAddressConverter.convert(org.getAddress().get(0));
        } else {
            return null;
        }
    }

}
