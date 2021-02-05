package uk.ac.ox.ndph.mts.site_service.service;

import uk.ac.ox.ndph.mts.site_service.exception.NotFoundException;
import uk.ac.ox.ndph.mts.site_service.model.Site;

import java.util.List;

/**
 * Interface for validating and saving an entity
 */
public interface SiteService {

    /**
     * Validate and save a site entity
     *
     * @param site the Site to save.
     * @return the id of the created entity.
     */
    String save(Site site);

    /**
     * Get all current sites
     * @return list of sites, should never be empty (always have a root node)
     */
    List<Site> findSites();

    /**
     * Find a site by ID,
     * @param id site ID, not null
     * @return site instance
     * @throws NotFoundException if ID of site not found
     */
    Site findSiteById(String id) throws NotFoundException;

}
