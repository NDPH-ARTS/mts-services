package uk.ac.ox.ndph.mts.role_service.service;

import org.springframework.http.HttpStatus;
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
    void whenAttemptToSaveDuplicateRole_thenAppropriateErrorThrown() {
        String duplicateRoleName = "foo";
        when(roleRepository.existsById(duplicateRoleName)).thenReturn(true);
        RoleService roleService = new RoleService(roleRepository, permissionRepository);
        Role r = new Role();
        r.setId(duplicateRoleName);
        assertThrows(DuplicateRoleException.class, () -> roleService.saveRole(r));
    }

    @Test
    void whenSaveNewRole_thenSuccess() {
        String newRoleName = "foo";
        Role r = new Role();
        r.setId(newRoleName);
        when(roleRepository.existsById(newRoleName)).thenReturn(false);
        when(roleRepository.save(r)).thenReturn(r);
        RoleService roleService = new RoleService(roleRepository, permissionRepository);
        assertNotNull(roleService.saveRole(r));
    }


    @Test
    void whenUpdatePermissions_givenNonExistentRole_thenThrowsNotFoundExcep() {
        String badRoleId = "foo";
        when(roleRepository.findById(badRoleId)).thenReturn(Optional.empty());
        RoleService roleService = new RoleService(roleRepository, permissionRepository);
        List<Permission> dummyPermissionList = Collections.singletonList(new Permission());
        ResponseStatusException thrown =  assertThrows(ResponseStatusException.class,  () -> roleService.updatePermissionsForRole(badRoleId, dummyPermissionList));
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    void whenUpdatePermissions_givenNonRegisteredPermission_thenThrowsBadRequestExcep() {
        String goodRoleId = "foo";
        Role goodRole = new Role();
        goodRole.setId(goodRoleId);

        String badPermissionName = "bar";
        Permission badPermission = new Permission();
        badPermission.setId(badPermissionName);

        when(roleRepository.findById(goodRoleId)).thenReturn(Optional.of(goodRole));
        when(permissionRepository.existsById(badPermissionName)).thenReturn(false);

        RoleService roleService = new RoleService(roleRepository, permissionRepository);
        List<Permission> badPermissionList = Collections.singletonList(badPermission);
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class,  () -> roleService.updatePermissionsForRole(goodRoleId, badPermissionList));
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }


}
