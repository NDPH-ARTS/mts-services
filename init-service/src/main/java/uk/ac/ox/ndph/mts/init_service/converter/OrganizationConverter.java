package uk.ac.ox.ndph.mts.init_service.converter;


import java.util.Collections;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import uk.ac.ox.ndph.mts.init_service.model.SiteAddressDTO;
import uk.ac.ox.ndph.mts.init_service.model.SiteDTO;

/**
 * Implement an EntityConverter for {@link Site} to {@link Organization}.
 */
@Component
public class OrganizationConverter implements EntityConverter<SiteDTO, org.hl7.fhir.r4.model.Organization> {

    EntityConverter<SiteAddressDTO, Address> fromAddressConverter;

    @Autowired
    public void setConverter(EntityConverter<SiteAddressDTO, Address> fromAddressConverter) {
        this.fromAddressConverter = fromAddressConverter;
    }

    /**
     * Convert a Site to an hl7 model Organization
     *
     * @param input the site to convert.
     * @return org.hl7.fhir.r4.model.Organization
     */
    public org.hl7.fhir.r4.model.Organization convert(SiteDTO input) {
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

        return fhirOrganization;
    }

    private void setParentOrganization(SiteDTO site, Organization fhirOrganization) {
        if (StringUtils.hasText(site.getParentSiteId())) {
            fhirOrganization.setPartOf(new Reference("Organization/" + site.getParentSiteId()));
        }
    }

}
