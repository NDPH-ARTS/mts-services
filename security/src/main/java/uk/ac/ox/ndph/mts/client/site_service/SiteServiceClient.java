package uk.ac.ox.ndph.mts.client.site_service;

import uk.ac.ox.ndph.mts.client.dtos.SiteDTO;

import java.util.List;

/**
 * Practitioner service client interface
 */
public interface SiteServiceClient {

    List<SiteDTO> getAllSites(String token);

}
