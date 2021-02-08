package uk.ac.ox.ndph.mts.site_service.service;

import org.springframework.web.server.ResponseStatusException;
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
     *
     * @return list of sites, should never be empty (always have a root node)
     */
    List<Site> findSites();

    /**
     * Find site by ID
     *
     * @param id site ID to search for
     * @return site if found
     * @throws ResponseStatusException if not found
     */
    Site findSiteById(String id) throws ResponseStatusException;

}
