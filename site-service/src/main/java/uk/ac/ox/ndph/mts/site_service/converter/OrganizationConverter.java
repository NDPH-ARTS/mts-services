package uk.ac.ox.ndph.mts.site_service.converter;

import uk.ac.ox.ndph.mts.site_service.model.Site;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Implement an EntityConverter for Site
 */
@Component
public class OrganizationConverter implements EntityConverter<Site, org.hl7.fhir.r4.model.Organization> {

    /**
     * Convert a Site to an hl7 model Organization with a random UUID.
     * @param input the site to convert.
     * @return org.hl7.fhir.r4.model.Organization
     */
    public org.hl7.fhir.r4.model.Organization convert(Site input) {
        org.hl7.fhir.r4.model.Organization fhirOrganization = new org.hl7.fhir.r4.model.Organization();

        fhirOrganization.setName(input.getName());
        fhirOrganization.addAlias(input.getAlias());

        String id = UUID.randomUUID().toString();
        fhirOrganization.setId(id);
        return fhirOrganization;
    }
}
