package uk.ac.ox.ndph.mts.role_service.service;

import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.role_service.service.LoggedRoleServiceException;
import uk.ac.ox.ndph.mts.role_service.model.Permission;
import uk.ac.ox.ndph.mts.role_service.model.PermissionRepository;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RoleServiceTest {


    @Mock
    RoleRepository roleRepository;


    @Mock
    PermissionRepository permissionRepository;


    @Test
    void whenAttemptToCreateDuplicateRole_thenThrowsConflictException() {
        String duplicateRoleName = "foo";
        when(roleRepository.existsById(duplicateRoleName)).thenReturn(true);
        RoleService roleService = new RoleService(roleRepository, permissionRepository);
        Role r = new Role();
        r.setId(duplicateRoleName);
        LoggedRoleServiceException thrown = assertThrows(LoggedRoleServiceException.class,
                () -> roleService.createRoleWithPermissions(r));
        assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
    }

    @Test
    void whenCreateNewRole_thenSuccess() {
        String newRoleName = "foo";
        String goodPermissionName = "good-permission";
        Role r = new Role();
        r.setId(newRoleName);
        Permission p = new Permission();
        p.setId(goodPermissionName);
        r.setPermissions(Collections.singletonList(p));
        when(roleRepository.existsById(newRoleName)).thenReturn(false);
        when(roleRepository.save(r)).thenReturn(r);
        when(permissionRepository.existsById(goodPermissionName)).thenReturn(true);
        RoleService roleService = new RoleService(roleRepository, permissionRepository);
        Role roleReturned = roleService.createRoleWithPermissions(r);
        assertNotNull(roleReturned);
        assertNotNull(roleReturned.getPermissions().get(0));
    }

    @Test
    void whenCreateNewRole_givenBadPermission_thenThrowsBadRequestExcep() {
        String newRoleName = "foo";
        Role r = new Role();
        r.setId(newRoleName);
        String badPermissionName = "bad-permission";
        Permission badPermission = new Permission();
        badPermission.setId(badPermissionName);
        r.setPermissions(Collections.singletonList(badPermission));

        when(permissionRepository.existsById(badPermissionName)).thenReturn(false);
        when(roleRepository.existsById(newRoleName)).thenReturn(false);
        RoleService roleService = new RoleService(roleRepository, permissionRepository);

        LoggedRoleServiceException thrown = assertThrows(LoggedRoleServiceException.class,
                () -> roleService.createRoleWithPermissions(r));
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());

    }


    @Test
    void whenUpdatePermissions_givenNonExistentRole_thenThrowsNotFoundExcep() {
        String badRoleId = "foo";
        when(roleRepository.findById(badRoleId)).thenReturn(Optional.empty());
        RoleService roleService = new RoleService(roleRepository, permissionRepository);
        List<Permission> dummyPermissionList = Collections.singletonList(new Permission());
        LoggedRoleServiceException thrown =  assertThrows(LoggedRoleServiceException.class,
                () -> roleService.updatePermissionsForRole(badRoleId, dummyPermissionList));
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    void whenUpdatePermissions_givenBadPermission_thenThrowsBadRequestExcep() {
        String goodRoleId = "foo";
        Role goodRole = new Role();
        goodRole.setId(goodRoleId);

        String badPermissionName = "bad-permission";
        Permission badPermission = new Permission();
        badPermission.setId(badPermissionName);

        String goodPermissionName = "good-permission";
        Permission goodPermission = new Permission();
        goodPermission.setId(goodPermissionName);

        when(roleRepository.findById(goodRoleId)).thenReturn(Optional.of(goodRole));
        when(permissionRepository.existsById(goodPermissionName)).thenReturn(true);
        when(permissionRepository.existsById(badPermissionName)).thenReturn(false);

        List<Permission> permissionList = List.of(goodPermission,badPermission);

        RoleService roleService = new RoleService(roleRepository, permissionRepository);

        LoggedRoleServiceException thrown = assertThrows(LoggedRoleServiceException.class,  () -> roleService.updatePermissionsForRole(goodRoleId, permissionList));
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
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
        when(roleRepository.save(existingRole)).thenReturn(existingRole);

        RoleService roleService = new RoleService(roleRepository, permissionRepository);
        assertNotNull(roleService.updatePermissionsForRole(roleName, Collections.singletonList(newPermissionForRole)));
    }


}
