package uk.ac.ox.ndph.mts.init_service.converter;


import java.util.Collections;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import uk.ac.ox.ndph.mts.init_service.model.SiteAddress;
import uk.ac.ox.ndph.mts.init_service.model.CoreAttributeNames;
import uk.ac.ox.ndph.mts.init_service.model.Site;

/**
 * Implement an EntityConverter for {@link Site} to {@link Organization}.
 */
@Component
public class OrganizationConverter implements EntityConverter<Site, org.hl7.fhir.r4.model.Organization> {

    EntityConverter<SiteAddress, Address> fromAddressConverter;

    @Autowired
    public void setConverter(EntityConverter<SiteAddress, Address> fromAddressConverter) {
        this.fromAddressConverter = fromAddressConverter;
    }

    /**
     * Convert a Site to an hl7 model Organization
     *
     * @param input the site to convert.
     * @return org.hl7.fhir.r4.model.Organization
     */
    public org.hl7.fhir.r4.model.Organization convert(Site input) {
        org.hl7.fhir.r4.model.Organization fhirOrganization = new org.hl7.fhir.r4.model.Organization();
        fhirOrganization.setName(input.getName());
        if (input.getAlias() != null) {
            fhirOrganization.addAlias(input.getAlias());
        }
        if (input.getSiteId() != null) {
            fhirOrganization.setId(new IdType(input.getSiteId()));
        }
        setParentOrganization(input, fhirOrganization);
        if (input.getSiteType() != null) {
            fhirOrganization.setImplicitRules(input.getSiteType());
        }

        if (input.getAddress() != null) {
            final Address address = fromAddressConverter.convert(input.getAddress());
            if (address != null) {
                fhirOrganization.setAddress(Collections.singletonList(address));
            }
        }
        if (input.getDescription() != null) {
            fhirOrganization.getText().getDiv().setName(CoreAttributeNames.DESCRIPTION.nameof());
            fhirOrganization.getText().getDiv().setValue(input.getDescription());
        }

        if (input.getStatus() != null) {
            fhirOrganization.setActive(input.getStatus().equals(Status.ACTIVE.getValue()));
        }

        if (input.getExtensions() != null) {
            input.getExtensions().entrySet().stream().forEach(e ->
                    fhirOrganization.addExtension(e.getKey(), new StringType(e.getValue())));
        }
        return fhirOrganization;
    }

    private void setParentOrganization(Site site, Organization fhirOrganization) {
        if (StringUtils.hasText(site.getParentSiteId())) {
            fhirOrganization.setPartOf(new Reference("Organization/" + site.getParentSiteId()));
        }
    }

}
