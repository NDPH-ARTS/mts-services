package uk.ac.ox.ndph.mts.site_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.site_service.configuration.SiteConfigurationProvider;
import uk.ac.ox.ndph.mts.site_service.exception.InitialisationError;
import uk.ac.ox.ndph.mts.site_service.exception.InvariantException;
import uk.ac.ox.ndph.mts.site_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.ValidationResponse;
import uk.ac.ox.ndph.mts.site_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.site_service.validation.ModelEntityValidation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implement an SiteServiceInterface interface.
 * Validation of site based on the input configuration regex fields before
 * sending the entity to store in the fhir repository.
 */
@Service
public class SiteServiceImpl implements SiteService {

    private final Map<String, SiteConfiguration> sitesByType = new HashMap<>();
    private final Map<String, String> parentTypeByChildType = new HashMap<>();
    private final EntityStore<Site, String> siteStore;
    private final ModelEntityValidation<Site> entityValidation;
    private final Logger logger = LoggerFactory.getLogger(SiteServiceImpl.class);

    /**
     * @param configurationProvider provide Site Configuration
     * @param siteStore        Site store interface
     * @param entityValidation Site validation interface
     */
    @Autowired
    public SiteServiceImpl(SiteConfigurationProvider configurationProvider,
                           final EntityStore<Site, String> siteStore,
                           ModelEntityValidation<Site> entityValidation) {
        var configuration = configurationProvider.getConfiguration();
        if (siteStore == null) {
            throw new InitialisationError("site store cannot be null");
        }
        if (entityValidation == null) {
            throw new InitialisationError("entity validation cannot be null");
        }
        this.siteStore = siteStore;
        this.entityValidation = entityValidation;
        initMaps(configuration);
        logger.info(Services.STARTUP.message());
    }

    private void initMaps(final SiteConfiguration configuration) {
        addTypesToMap(configuration);
    }

    private void addTypesToMap(final SiteConfiguration configuration) {
        this.sitesByType.put(configuration.getType(), configuration);
        if (configuration.getChild() != null) {
            for (final var childConfig : configuration.getChild()) {
                addTypesToMap(childConfig);
                this.parentTypeByChildType.put(childConfig.getType(), configuration.getType());
            }
        }
    }

    /**
     * @param site the Site to save.
     * @return The id of the new site
     */
    @Override
    public String save(final Site site) {
        var validationResponse = entityValidation.validate(site);

        if (!validationResponse.isValid()) {
            throw new ValidationException(validationResponse.getErrorMessage());
        }

        if (findSiteByName(site.getName()).isPresent()) {
            throw new ValidationException(Services.SITE_EXISTS.message());
        }

        if (site.getParentSiteId() == null) {
            if (isRootSitePresent()) {
                throw new ValidationException(Services.ONE_ROOT_SITE.message());
            }
        } else {
            validationResponse = validateParentSiteExists(site.getParentSiteId());
            if (!validationResponse.isValid()) {
                throw new ValidationException(validationResponse.getErrorMessage());
            } else {
                Site siteParent = findSiteById(site.getParentSiteId());
                String siteParentType = siteParent.getSiteType();
                String allowedParentType = parentTypeByChildType.get(site.getSiteType());
                if (!siteParentType.equalsIgnoreCase(allowedParentType)) {
                    // valid parent(parent Id), invalid Child Type
                    // diff  parent(parent Id), valid Child Type
                    throw new ValidationException(Services.INVALID_PARENT_CHILD.message());
                }
                // valid parent(parent Id), valid Child Type
            }
        }
        return siteStore.saveEntity(site);
    }

    /**
     * Get complete sites list.  Note the list should never be empty if the trial has been initialized, and
     * this method should not be called if the trial has not been initialized. So throws an exception if the
     * store returns an empty sites list.
     *
     * @return list of sites, never empty
     * @throws InvariantException if the list from the store is empty
     */
    @Override
    public List<Site> findSites() {
        final List<Site> sites = this.siteStore.findAll();
        if (sites.isEmpty()) {
            throw new InvariantException(Services.NO_ROOT_SITE.message());
        }
        return sites;
    }

    private ValidationResponse validateParentSiteExists(final String parentSiteId) {
        if (this.siteStore.findById(parentSiteId).isPresent()) {
            return new ValidationResponse(true, "");
        }
        return new ValidationResponse(false, Services.PARENT_NOT_FOUND.message());
    }

    /**
     * Find Site By Site Name
     *
     * @param siteName the Site to search.
     * @return site The Site being searched, or none() if not found
     */
    Optional<Site> findSiteByName(String siteName) {
        return siteStore.findByName(siteName);
    }

    /**
     * Find site by ID
     *
     * @param id site ID to search for
     * @return site if found
     * @throws ResponseStatusException if not found
     */
    public Site findSiteById(String id) throws ResponseStatusException {
        return this.siteStore
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, Services.SITE_NOT_FOUND.message()));

    }

    /**
     * Test if the root node is present. If not, then the site service cannot be safely used,
     * since the trial has not yet been initialized.
     *
     * @return true if a root site node is present
     */
    private boolean isRootSitePresent() {
        return this.siteStore
                .findRoot()
                .isPresent();
    }

    /**
     * Return the root node, throwing if not present
     *
     * @return site if found
     * @throws ResponseStatusException if no root site (bad trial initialization)
     */
    Site findRootSite() throws ResponseStatusException {
        return this.siteStore
                .findRoot()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Services.NO_ROOT_SITE.message()));
    }


}
