package uk.ac.ox.ndph.mts.site_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.ox.ndph.mts.site_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.site_service.exception.InvariantException;
import uk.ac.ox.ndph.mts.site_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.site_service.validation.ModelEntityValidation;

import java.util.List;

/**
 * Implement an SiteServiceInterface interface.
 * Validation of site based on the input configuration regex fields before
 * sending the entity to store in the fhir repository.
 */
@Service
public class SiteServiceImpl implements SiteService {

    private EntityStore<Site> siteStore;
    private final ModelEntityValidation<Site> entityValidation;
    private final Logger logger = LoggerFactory.getLogger(SiteServiceImpl.class);

    /**
     *
     * @param siteStore Site store interface
     * @param entityValidation Site validation interface 
     */
    @Autowired
    public SiteServiceImpl(EntityStore<Site> siteStore,
                           ModelEntityValidation<Site> entityValidation) {
        if (siteStore == null) {
            throw new InitialisationError("site store cannot be null");
        }
        if (entityValidation == null) {
            throw new InitialisationError("entity validation cannot be null");
        }
        this.siteStore = siteStore;
        this.entityValidation = entityValidation;

        logger.info(Services.STARTUP.message());

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
        return siteStore.saveEntity(site);
    }

    /**
     * Get complete sites list
     * @return list of sites, never empty
     * @throws InvariantException if the list from the store is empty
     */
    @GetMapping
    public List<Site> getSites() {
        final List<Site> sites = this.siteStore.geAllEntities();
        if (sites.isEmpty()) {
            throw new InvariantException(Services.NO_ROOT_SITE.message());
        }
        return sites;
    }

}
