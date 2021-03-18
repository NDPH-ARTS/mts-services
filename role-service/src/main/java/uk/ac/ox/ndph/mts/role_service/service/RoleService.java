package uk.ac.ox.ndph.mts.role_service.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.role_service.controller.DuplicateRoleException;
import uk.ac.ox.ndph.mts.role_service.model.Permission;
import uk.ac.ox.ndph.mts.role_service.model.PermissionRepository;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role createRoleWithPermissions(Role role) throws DuplicateRoleException {

        logger.debug("Creating role with permissions");
        if (roleRepository.existsById(role.getId())) {
            logger.debug("Duplicate role");

            throw new DuplicateRoleException();
        }

        validatePermissions(role.getPermissions());

        Role roleResult = roleRepository.save(role);
        logger.debug("Created role with permissions");
        return roleResult;
    }

    public Role updatePermissionsForRole(String roleId, List<Permission> newPermissions)
            throws ResponseStatusException {

        logger.debug("Updating permissions for role");
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (roleOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format(ResponseMessages.ROLE_NOT_FOUND.message(), roleId));
        }
        validatePermissions(newPermissions);

        Role role = roleOptional.get();
        role.setPermissions(newPermissions);
        Role roleResult = roleRepository.save(role);
        logger.debug("Updated permissions for role");
        return roleResult;
    }


    private void validatePermissions(List<Permission> newPermissions) throws ResponseStatusException {
        if (newPermissions == null) {
            return;
        }
        for (Permission newPermission : newPermissions) {
            if (!permissionRepository.existsById(newPermission.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        String.format(ResponseMessages.PERMISSION_NOT_FOUND.message(), newPermission.getId()));
            }
        }
    }

}
