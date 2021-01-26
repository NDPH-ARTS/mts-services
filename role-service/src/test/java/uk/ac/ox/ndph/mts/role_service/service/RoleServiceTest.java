package uk.ac.ox.ndph.mts.role_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.role_service.controller.DuplicateRoleException;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RoleServiceTest {


    @Mock
    RoleRepository roleRepository;


    @Test
    void whenAttemptToAddDuplicateRole_AppropriateErrorThrown() {
        String duplicateRoleName = "foo";
        when(roleRepository.existsById(duplicateRoleName)).thenReturn(true);
        RoleService roleService = new RoleService(roleRepository);
        Role r = new Role();
        r.setId(duplicateRoleName);
        assertThrows(DuplicateRoleException.class, () -> roleService.saveRole(r));
    }


}
