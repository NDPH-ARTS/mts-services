package uk.ac.ox.ndph.mts.role_service.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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

    public Role createRoleWithPermissions(Role role) throws ResponseStatusException {

        if (roleRepository.existsById(role.getId())) {
            throw ResponseFactory.loggedException(HttpStatus.CONFLICT,
                    String.format(ResponseMessages.DUPLICATE_ROLE_ID.message(), role.getId()));
        }

        validatePermissions(role.getPermissions());

        return roleRepository.save(role);
    }

    public Role updatePermissionsForRole(String roleId, List<Permission> newPermissions)
            throws ResponseStatusException {

        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (roleOptional.isEmpty()) {
            throw ResponseFactory.loggedException(HttpStatus.NOT_FOUND,
                    String.format(ResponseMessages.ROLE_NOT_FOUND.message(), roleId));
        }
        validatePermissions(newPermissions);

        Role role = roleOptional.get();
        role.setPermissions(newPermissions);
        return roleRepository.save(role);
    }


    private void validatePermissions(List<Permission> newPermissions) throws ResponseStatusException {
        if (newPermissions == null) {
            return;
        }
        for (Permission newPermission : newPermissions) {
            if (!permissionRepository.existsById(newPermission.getId())) {
                throw ResponseFactory.loggedException(
                        HttpStatus.BAD_REQUEST,
                        String.format(ResponseMessages.PERMISSION_NOT_FOUND.message(), newPermission.getId()));
            }
        }
    }

}
