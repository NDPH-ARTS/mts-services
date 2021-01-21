package uk.ac.ox.ndph.mts.role_service.service;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ox.ndph.mts.role_service.controller.DuplicateRoleException;
import uk.ac.ox.ndph.mts.role_service.model.Permission;
import uk.ac.ox.ndph.mts.role_service.model.PermissionRepository;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;

import java.util.Collections;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RoleServiceTest {


    @Mock
    RoleRepository roleRepository;


    @Mock
    PermissionRepository permissionRepository;


    @Test
    void whenAttemptToAddDuplicateRole_AppropriateErrorThrown() {
        String duplicateRoleName = "foo";
        when(roleRepository.existsById(duplicateRoleName)).thenReturn(true);
        RoleService roleService = new RoleService(roleRepository, permissionRepository);
        Role r = new Role();
        r.setId(duplicateRoleName);
        assertThrows(DuplicateRoleException.class, () -> roleService.saveRole(r));
    }

    @Test
    void whenUpdatePermissions_givenValidRoleAndValidPerm_thenSuccess() {
        String roleName = "foo";
        Role existingRole = new Role();
        existingRole.setId(roleName);

        String permName = "bar";
        Permission newPermissionForRole = new Permission();
        newPermissionForRole.setId(permName);

        when(roleRepository.findById(roleName)).thenReturn(Optional.of(existingRole));
        when(permissionRepository.existsById(permName)).thenReturn(true);

        RoleService roleService = new RoleService(roleRepository, permissionRepository);
        roleService.updatePermissionsForRole(roleName, Collections.singletonList(newPermissionForRole));
    }

    @Test
    void whenUpdatePermissions_givenNonExistentRole_thenThrowsAppropriateExcep() {
        String badRoleId = "foo";
        when(roleRepository.findById(badRoleId)).thenReturn(Optional.empty());
        RoleService roleService = new RoleService(roleRepository, permissionRepository);
        ResponseStatusException thrown =  assertThrows(ResponseStatusException.class,  () -> roleService.updatePermissionsForRole(badRoleId, Collections.singletonList(new Permission())));
        assertEquals(thrown.getStatus().value(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void whenUpdatePermissions_givenNonExistentPermission_thenThrowsAppropriateExcep() {
        String goodRoleId = "foo";
        Role goodRole = new Role();
        goodRole.setId(goodRoleId);

        String badPermissionName = "bar";
        Permission badPermission = new Permission();
        badPermission.setId(badPermissionName);

        when(roleRepository.findById(goodRoleId)).thenReturn(Optional.of(goodRole));
        when(permissionRepository.existsById(badPermissionName)).thenReturn(false);

        RoleService roleService = new RoleService(roleRepository, permissionRepository);

        ResponseStatusException thrown = assertThrows(ResponseStatusException.class,  () -> roleService.updatePermissionsForRole(goodRoleId, Collections.singletonList(badPermission)));
        assertEquals(thrown.getStatus().value(), HttpStatus.SC_BAD_REQUEST);
    }


}
