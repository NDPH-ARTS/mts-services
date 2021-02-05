package uk.ac.ox.ndph.mts.site_service.service;

import uk.ac.ox.ndph.mts.site_service.exception.NotFoundException;
import uk.ac.ox.ndph.mts.site_service.model.Site;

import java.util.List;
import java.util.Optional;

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
     * Find Site By Site Name
     *
     * @param siteName the Site to search.
     * @return site The Site being searched, or none() if not found
     */
    Optional<Site> findSiteByName(String siteName);

    /**
     * Find site by ID
     * @param id site ID to search for
     * @return site if found
     * @throws NotFoundException if not found
     */
    Site findSiteById(String id) throws NotFoundException;


}
