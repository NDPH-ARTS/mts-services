package uk.ac.ox.ndph.mts.site_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.site_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.site_service.exception.InvariantException;
import uk.ac.ox.ndph.mts.site_service.exception.NotFoundException;
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

    private EntityStore<String, Site> siteStore;
    private final ModelEntityValidation<Site> entityValidation;
    private final Logger logger = LoggerFactory.getLogger(SiteServiceImpl.class);

    /**
     *
     * @param siteStore Site store interface
     * @param entityValidation Site validation interface 
     */
    @Autowired
    public SiteServiceImpl(EntityStore<String, Site> siteStore,
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
     * Get complete sites list.  Note the list should never be empty if the trial has been initialized, and
     * this method should not be called if the trial has not been initialized. So throws an exception if the
     * store returns an empty sites list.
     * @return list of sites, never empty
     * @throws InvariantException if the list from the store is empty
     */
    public List<Site> findSites() {
        final List<Site> sites = this.siteStore.findAll();
        if (sites.isEmpty()) {
            throw new InvariantException(Services.NO_ROOT_SITE.message());
        }
        return sites;
    }

    /**
     * Find a site by ID,
     * @param id site ID, not null
     * @return site instance
     * @throws NotFoundException if site with that iD not found
     */
    public Site findSiteById(final String id) throws NotFoundException {
        return this.siteStore
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Services.SITE_NOT_FOUND.message(), id));
    }

}
