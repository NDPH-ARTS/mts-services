package uk.ac.ox.ndph.mts.site_service.converter;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.helper.FHIRClientHelper;
import uk.ac.ox.ndph.mts.site_service.model.Site;

import java.util.Optional;
import java.util.UUID;

/**
 * Implement an EntityConverter for Site
 */
@Component
public class OrganizationConverter implements EntityConverter<Site, org.hl7.fhir.r4.model.Organization> {

    private FHIRClientHelper fhirClientHelper;

    /**
     * Convert a Site to an hl7 model Organization with a random UUID.
     * @param input the site to convert.
     * @return org.hl7.fhir.r4.model.Organization
     */
    public org.hl7.fhir.r4.model.Organization convert(Site input) {
        org.hl7.fhir.r4.model.Organization fhirOrganization = new org.hl7.fhir.r4.model.Organization();

        fhirOrganization.setName(input.getName());
        fhirOrganization.addAlias(input.getAlias());

        setParentOrganization(input, fhirOrganization);

        //String id = UUID.randomUUID().toString();

        //Identifier identifier = new Identifier();
        //identifier.setValue(id);
        //fhirOrganization.addIdentifier(identifier);

        //fhirOrganization.setId(id);
        //fhirOrganization.addIdentifier().setSystem("http://localhost:8080").setValue(id);

        return fhirOrganization;
    }

    private void setParentOrganization(Site site, Organization fhirOrganization) {
        Optional<String> optParentSiteId = Optional.ofNullable(site.getParentSiteId());
        if (optParentSiteId.isPresent()) {
            try {
                fhirClientHelper = new FHIRClientHelper("http://localhost:8080");

                String parentSiteId = optParentSiteId.get();

                Organization orgFound = fhirClientHelper.findOrganizationByID(parentSiteId);

                Reference fhirReferenceOrgFound = orgFound.getPartOf();

                // Create Identifier
                Identifier identifierParentOrg = new Identifier();
                identifierParentOrg.setValue(parentSiteId);

                // Create Reference
                Reference fhirReferenceOrg = new Reference();
                fhirReferenceOrg.setIdentifier(identifierParentOrg);

                // Set the Parent Organization
                fhirOrganization.setPartOf(fhirReferenceOrg);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
