package uk.ac.ox.ndph.mts.client.role_service;

import uk.ac.ox.ndph.mts.client.dtos.RoleDTO;

import java.util.List;

/**
 * Role service client interface
 */
public interface RoleServiceClient {

    List<RoleDTO> getRolesByIds(List<String> roleIds);

}
