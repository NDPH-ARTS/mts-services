package uk.ac.ox.ndph.mts.sample_service.client.role_service;

import uk.ac.ox.ndph.mts.sample_service.client.dtos.RoleDTO;

/**
 * Role service client interface
 */
public interface RoleServiceClient {

    RoleDTO getRolesById(String roleId);

}
