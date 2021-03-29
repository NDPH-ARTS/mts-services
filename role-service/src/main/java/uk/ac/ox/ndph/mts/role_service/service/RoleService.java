package uk.ac.ox.ndph.mts.role_service.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.role_service.model.Permission;
import uk.ac.ox.ndph.mts.role_service.model.PermissionRepository;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {


    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role createRoleWithPermissions(Role role) throws LoggedRoleServiceException {

        if (roleRepository.existsById(role.getId())) {
            throw new LoggedRoleServiceException(HttpStatus.CONFLICT,
                    String.format(ResponseMessages.DUPLICATE_ROLE_ID.message(), role.getId()));
        }

        validatePermissions(role.getPermissions());

        return roleRepository.save(role);
    }

    public Role updatePermissionsForRole(String roleId, List<Permission> newPermissions)
            throws LoggedRoleServiceException {

        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (roleOptional.isEmpty()) {
            throw new LoggedRoleServiceException(HttpStatus.NOT_FOUND,
                    String.format(ResponseMessages.ROLE_NOT_FOUND.message(), roleId));
        }
        validatePermissions(newPermissions);

        Role role = roleOptional.get();
        role.setPermissions(newPermissions);
        return roleRepository.save(role);
    }


    private void validatePermissions(List<Permission> newPermissions) throws LoggedRoleServiceException {
        if (newPermissions == null) {
            return;
        }
        for (Permission newPermission : newPermissions) {
            if (!permissionRepository.existsById(newPermission.getId())) {
                throw new LoggedRoleServiceException(
                        HttpStatus.BAD_REQUEST,
                        String.format(ResponseMessages.PERMISSION_NOT_FOUND.message(), newPermission.getId()));
            }
        }
    }

}
