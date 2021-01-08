package uk.ac.ox.ndph.mts.role_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;


import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    private RoleController roleController;

    @Mock
    RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roleController = new RoleController(roleRepository);
    }



    @Test
    void createRole_addsAuditData() {

        Role testRole = new Role();
        testRole.setRoleName("testRoleName");

        when(roleRepository.save(Mockito.any(Role.class))).thenAnswer(i -> i.getArguments()[0]);
        Role savedRole = roleController.create(testRole);

        assertEquals(savedRole.getRoleName(), testRole.getRoleName());

        assertNotNull(savedRole.getCreatedDateTime());
        assertNotNull(savedRole.getCreatedBy());
    }


}
