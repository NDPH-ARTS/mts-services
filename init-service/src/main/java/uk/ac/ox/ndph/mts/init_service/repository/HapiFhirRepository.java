package uk.ac.ox.ndph.mts.init_service.repository;

import static java.lang.String.format;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.ac.ox.ndph.mts.init_service.exception.RestException;

/**
 * Implement FhirRepository interface using HAPI client sdk and backed up by
 * a FHIR store.
 */
@Component
public class HapiFhirRepository implements FhirRepository {

    private final FhirContextWrapper fhirContextWrapper;
    private final Logger logger = LoggerFactory.getLogger(HapiFhirRepository.class);

    @Value("${fhir.uri}")
    private String fhirUri = "";

    HapiFhirRepository(FhirContextWrapper fhirContextWrapper) {
        this.fhirContextWrapper = fhirContextWrapper;
    }

    /**
     * Save a single resource of any type to the FHIR API, returning the allocated ID
     *
     * @param resource the resource to create
     * @return id of the created resource
     */
    private String saveResource(final Resource resource) {
        logger.info(format(Repository.REQUEST_PAYLOAD.message(),
            fhirContextWrapper.prettyPrint(resource)));
        Bundle responseBundle;
        try {
            responseBundle = fhirContextWrapper.executeTransaction(fhirUri, 
                bundle(resource, resource.getResourceType().name()));
        } catch (FhirServerResponseException e) {
            throw new RestException(Repository.UPDATE_ERROR.message(), e);
        }
        IBaseResource responseElement = extractResponseResource(responseBundle);
        logger.info(format(Repository.RESPONSE_PAYLOAD.message(), fhirContextWrapper.prettyPrint(responseElement)));

        return responseElement.getIdElement().getIdPart();
    }

    @Override
    public String saveOrganization(final Organization organization) {
        return saveResource(organization);
    }

    @Override
    public String savePractitioner(final Practitioner practitioner) {
        return saveResource(practitioner);
    }
    
    @Override
    public String savePractitionerRole(final PractitionerRole practitionerRole) {
        return saveResource(practitionerRole);
    }

    /**
     * @param researchStudy the researchStudy to save.
     * @return ResearchStudy
     */
    public String saveResearchStudy(final ResearchStudy researchStudy) {
        return saveResource(researchStudy);
    }
 
    private IBaseResource extractResponseResource(Bundle bundle) throws RestException {
        var resp = fhirContextWrapper.toListOfResources(bundle);

        if (resp.size() != 1) {
            logger.info(format("Incorrect response size!", resp.size()));
            throw new RestException(format("Incorrect response size!", resp.size()));
        }
        return resp.get(0);
    }
    
    private Bundle bundle(Resource resource, String resourceName) {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.TRANSACTION);

        bundle.addEntry().setFullUrl(resource.getIdElement().getValue()).setResource(resource).getRequest()
            .setUrl(resourceName).setMethod(Bundle.HTTPVerb.POST);
        return bundle;
    }

}
