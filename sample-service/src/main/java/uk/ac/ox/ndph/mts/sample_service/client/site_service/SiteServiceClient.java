package uk.ac.ox.ndph.mts.sample_service.client.site_service;

import uk.ac.ox.ndph.mts.sample_service.client.dtos.AssignmentRoleDTO;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.SiteDTO;

/**
 * Practitioner service client interface
 */
public interface SiteServiceClient {

    SiteDTO[] getAllSites();

}
