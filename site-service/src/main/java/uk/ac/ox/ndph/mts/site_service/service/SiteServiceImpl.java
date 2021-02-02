package uk.ac.ox.ndph.mts.site_service.service;

import org.hl7.fhir.r4.model.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.site_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.site_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.site_service.repository.FhirRepository;
import uk.ac.ox.ndph.mts.site_service.validation.ModelEntityValidation;

/**
 * Implement an SiteServiceInterface interface.
 * Validation of site based on the input configuration regex fields before
 * sending the entity to store in the fhir repository.
 */
@Service
public class SiteServiceImpl implements SiteService {

    private final FhirRepository repository;
    private EntityStore<Site> siteStore;
    private final ModelEntityValidation<Site> entityValidation;
    private final Logger logger = LoggerFactory.getLogger(SiteServiceImpl.class);

    /**
     *
     * @param repository - The fhir repository
     * @param siteStore Site store interface
     * @param entityValidation Site validation interface 
     */
    @Autowired
    public SiteServiceImpl(FhirRepository repository, EntityStore<Site> siteStore,
                           ModelEntityValidation<Site> entityValidation) {
        if (repository == null) {
            throw new InitialisationError("repository cannot be null");
        }
        if (siteStore == null) {
            throw new InitialisationError("site store cannot be null");
        }
        if (entityValidation == null) {
            throw new InitialisationError("entity validation cannot be null");
        }

        this.repository = repository;
        this.siteStore = siteStore;
        this.entityValidation = entityValidation;

        if (logger.isInfoEnabled()) {
            logger.info(Services.STARTUP.message());
        }

    }

    /**
     *
     * @param site the Site to save.
     * @return The id of the new site
     */
    public String save(Site site) {
        var validationResponse = entityValidation.validate(site);
        if (!validationResponse.isValid()) {
            throw new ValidationException(validationResponse.getErrorMessage());
        }

        //Check if the Organization already exists.
        Organization org = repository.findOrganizationByName(site.getName());

        if (null == org) {
            return siteStore.saveEntity(site);
        } else {
            throw new ValidationException(validationResponse.getErrorMessage());
        }
    }
}
