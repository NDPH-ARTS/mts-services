package uk.ac.ox.ndph.mts.site_service.service;

import uk.ac.ox.ndph.mts.site_service.model.Site;

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
}