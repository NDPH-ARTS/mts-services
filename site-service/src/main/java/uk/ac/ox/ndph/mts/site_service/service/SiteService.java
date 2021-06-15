package uk.ac.ox.ndph.mts.site_service.service;

import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.SiteDTO;
import uk.ac.ox.ndph.mts.site_service.model.SiteNameDTO;

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
    List<SiteDTO> findSites();

    /**
     * Find site by ID
     *
     * @param id site ID to search for
     * @return site if found
     * @throws ResponseStatusException if not found
     */
    Site findSiteById(String id) throws ResponseStatusException;

    /**
     * Get all parent siteIds
     *
     * @return list of siteIds, should never be empty (always have a root node)
     */
    List<String> findParentSiteIds(String siteId);

    boolean filterUserSites(List<SiteDTO> sitesReturnObject, String userRole, String accessPerm);

    List<SiteNameDTO> findAssignedSites();

}
