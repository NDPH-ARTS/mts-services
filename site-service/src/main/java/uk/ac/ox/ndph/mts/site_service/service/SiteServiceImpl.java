package uk.ac.ox.ndph.mts.site_service.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.site_service.exception.InvariantException;
import uk.ac.ox.ndph.mts.site_service.exception.ValidationException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;
import uk.ac.ox.ndph.mts.site_service.repository.EntityStore;
import uk.ac.ox.ndph.mts.site_service.validation.ModelEntityValidation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
     * @param configuration    injected site configuration
     * @param siteStore        Site store interface
     * @param entityValidation Site validation interface
     */
    @Autowired
    public SiteServiceImpl(final SiteConfiguration configuration,
                           final EntityStore<Site, String> siteStore,
                           final ModelEntityValidation<Site> entityValidation) {
        Objects.requireNonNull(configuration, "site configuration cannot be null");
        Objects.requireNonNull(siteStore, "site store cannot be null");
        Objects.requireNonNull(entityValidation, "entity validation cannot be null");
        this.siteStore = siteStore;
        this.entityValidation = entityValidation;
        addTypesToMap(configuration);
        logger.info(Services.STARTUP.message());
    }

    private void addTypesToMap(final SiteConfiguration configuration) {
        this.sitesByType.put(configuration.getType(), configuration);
        for (final var childConfig : CollectionUtils.emptyIfNull(configuration.getChild())) {
            addTypesToMap(childConfig);
            this.parentTypeByChildType.put(childConfig.getType(), configuration.getType());
        }
    }

    /**
     * @param site the Site to save.
     * @return The id of the new site
     */
    @Override
    public String save(final Site site) {
        var validationCoreAttributesResponse = entityValidation.validateCoreAttributes(site);
        var validationCustomAttributesResponse = entityValidation.validateCustomAttributes(site);
        var validationExtAttributesResponse = entityValidation.validateExtAttributes(site);


        String siteTypeForSite = StringUtils.defaultString(site.getSiteType());
        if (!validationCoreAttributesResponse.isValid()) {
            throw new ValidationException(validationCoreAttributesResponse.getErrorMessage());
        }

        if (!validationCustomAttributesResponse.isValid()) {
            throw new ValidationException(validationCustomAttributesResponse.getErrorMessage());
        }

        if (!validationExtAttributesResponse.isValid()) {
            throw new ValidationException(validationExtAttributesResponse.getErrorMessage());
        }

        if (siteStore.existsByName(site.getName())) {
            throw new ValidationException(Services.SITE_NAME_EXISTS.message());
        }

        if (site.getParentSiteId() == null) {
            if (isRootSitePresent()) {
                throw new ValidationException(Services.ROOT_SITE_EXISTS.message());
            } else {
                if (!sitesByType.containsKey(siteTypeForSite) || parentTypeByChildType.containsKey(siteTypeForSite)) {
                    throw new ValidationException(Services.INVALID_ROOT_SITE.message());
                }
            }
        } else {
            Site siteParent = findSiteById(site.getParentSiteId());
            String siteParentType = siteParent.getSiteType();
            String allowedParentType = parentTypeByChildType.get(siteTypeForSite);
            if (!siteParentType.equalsIgnoreCase(allowedParentType)) {
                throw new ValidationException(Services.INVALID_CHILD_SITE_TYPE.message());
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
