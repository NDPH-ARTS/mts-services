package uk.ac.ox.ndph.mts.site_service.converter;


import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.ValidationResponse;
import uk.ac.ox.ndph.mts.site_service.repository.FhirRepo;
import uk.ac.ox.ndph.mts.site_service.repository.FhirRepository;

/**
 * Implement an EntityConverter for Site
 */
@Component
public class OrganizationConverter implements EntityConverter<Site, org.hl7.fhir.r4.model.Organization> {

    private final FhirRepository repository;

    /**
     *
     * @param repository - The fhir repository
     */
    @Autowired
    public OrganizationConverter(FhirRepository repository) {
        this.repository = repository;
    }

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

        return fhirOrganization;
    }

    private void setParentOrganization(Site site, Organization fhirOrganization) {
        if (!isNullOrBlank(site.getParentSiteId())) {

            var validationResponse = validOrganizationFound(site);
            if (validationResponse.isValid()) {
                // Set the Parent Organization if and only if it exists.
                fhirOrganization.setPartOf(new Reference("Organization/" + site.getParentSiteId()));
            }
        }
    }

    private ValidationResponse validOrganizationFound(Site site) {
        // Check if the Parent Organization already exists.
        Organization orgFound = repository.findOrganizationByID(site.getParentSiteId());

        if (null != orgFound && orgFound.getIdElement().getIdPart().equalsIgnoreCase(site.getParentSiteId())) {
            return new ValidationResponse(true, "");
        }
        return new ValidationResponse(false, FhirRepo.SITE_DOESNT_MATCH_PARENT.message());
    }

    private boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }
}
