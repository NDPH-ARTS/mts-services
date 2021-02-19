package uk.ac.ox.ndph.mts.roleserviceclient;

import org.springframework.data.domain.Page;
import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;

import java.util.List;

public interface EntityServiceClient {

    boolean roleIdExists(String entityId);

    List<RoleDTO> getRolesByIds(List<String> roleIds);

    RoleDTO getRoleById(String id);

    Page<RoleDTO> getPaged(int page, int size);

    RoleDTO createRole(RoleDTO role);

    RoleDTO updatePermissions(String roleId, List<PermissionDTO> permissionsDTOs);
}
