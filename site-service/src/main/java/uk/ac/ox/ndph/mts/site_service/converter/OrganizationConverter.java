package uk.ac.ox.ndph.mts.site_service.converter;


import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.ac.ox.ndph.mts.site_service.model.Site;

/**
 * Implement an EntityConverter for {@link Site} to {@link Organization}.
 */
@Component
public class OrganizationConverter implements EntityConverter<Site, org.hl7.fhir.r4.model.Organization> {

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
            fhirOrganization.addAddress().addLine(input.getAddress().getAddress1())
                    .addLine(input.getAddress().getAddress2())
                    .addLine(input.getAddress().getAddress3())
                    .addLine(input.getAddress().getAddress4())
                    .addLine(input.getAddress().getAddress5())
                    .setCity(input.getAddress().getCity())
                    .setCountry(input.getAddress().getCountry())
                    .setPostalCode(input.getAddress().getPostcode());
        }
        return fhirOrganization;
    }

    private void setParentOrganization(Site site, Organization fhirOrganization) {
        if (StringUtils.hasText(site.getParentSiteId())) {
            fhirOrganization.setPartOf(new Reference("Organization/" + site.getParentSiteId()));
        }
    }

}
