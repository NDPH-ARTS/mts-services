package uk.ac.ox.ndph.mts.sample_service.client.site_service;

import uk.ac.ox.ndph.mts.sample_service.client.dtos.SiteDTO;

import java.util.List;

/**
 * Practitioner service client interface
 */
public interface SiteServiceClient {

    List<SiteDTO> getAllSites();

}
