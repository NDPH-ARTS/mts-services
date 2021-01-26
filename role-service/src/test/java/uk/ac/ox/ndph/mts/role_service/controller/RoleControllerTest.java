package uk.ac.ox.ndph.mts.role_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import uk.ac.ox.ndph.mts.role_service.controller.dtos.PermissionDTO;
import uk.ac.ox.ndph.mts.role_service.model.Permission;
import uk.ac.ox.ndph.mts.role_service.model.PermissionRepository;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.controller.dtos.RoleDTO;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;
import uk.ac.ox.ndph.mts.role_service.service.RoleService;

import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Optional;
import java.util.logging.Logger;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RunWith(SpringRunner.class)
@WebMvcTest(RoleController.class)
class RoleControllerTest {


    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper jsonMapper;

    @MockBean
    private RoleRepository roleRepo;

    @MockBean
    private PermissionRepository permissionRepo;

    @MockBean
    private RoleService roleService;

    @MockBean
    private ModelMapper modelMapper;


    @Test
    void whenPostValidRole_thenReturnSuccess()
            throws Exception {

        String dummyName = "Dummy role name";
        RoleDTO role = new RoleDTO();
        role.setId(dummyName);

        String jsonRole = jsonMapper.writeValueAsString(role);

        mvc.perform(post("/roles/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRole)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

    }

    @Test
    void whenPostInvalidRole_thenReturn400()
            throws Exception {

        RoleDTO role = new RoleDTO(); // no name

        String jsonRole = jsonMapper.writeValueAsString(role);

        mvc.perform(post("/roles/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRole)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }

    @Test
    void whenConvertRoleDtoToEntity_thenSameData() {

        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId("role-test-id");
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setId("perm-test-id");
        roleDTO.setPermissions(Collections.singletonList(permissionDTO));

        RoleController c = new RoleController(roleRepo, roleService, new ModelMapper());
        Role roleEntity = c.convertDtoToEntity(roleDTO, Role.class);

        assertEquals(roleEntity.getId(), roleDTO.getId());
        assertTrue(roleEntity.getPermissions().stream().anyMatch(perm->perm.getId().equals(permissionDTO.getId())));

    }

    @Test
    void whenGetRolesPaged_thenReceiveSuccess()
            throws Exception {

        mvc.perform(get("/roles?page=0&size=10")).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk());
    }


    @Test
    void whenGetOneRole_thenReceiveSuccess()
            throws Exception {

        Role dummyRole = new Role();
        String dummyId="dummy-role";
        dummyRole.setId(dummyId);

        when(roleRepo.findById(dummyId)).thenReturn(Optional.of(dummyRole));
        mvc.perform(get("/roles/"+dummyId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(dummyId)));
    }

    @Test
    void whenGetNonExistentRole_thenReceive404()
            throws Exception {

        mvc.perform(get("/roles/nonexistentrole"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }

    @Test
    void whenGetRole_thenAlsoReceivePermissionsForRole()
            throws Exception {

        Role dummyRole = new Role();
        String dummyRoleId="dummy-role";
        dummyRole.setId(dummyRoleId);
        Permission dummyPermission = new Permission();
        String dummyPermissionId="dummy-permission";
        dummyPermission.setId("dummy-permission");
        dummyRole.setPermissions(Collections.singletonList(dummyPermission));

        when(roleRepo.findById(dummyRoleId)).thenReturn(Optional.of(dummyRole));
        mvc.perform(get("/roles/"+dummyRoleId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(dummyRoleId)))
                .andExpect(content().string(containsString(dummyPermissionId)));
    }

    @Test
    void whenPostValidPermissionForRole_thenReceiveSuccess()
            throws Exception {


        String dummyRoleId="dummy-role";
        String dummyPermissionId="dummy-permission";

        PermissionDTO permDTO = new PermissionDTO();
        permDTO.setId(dummyPermissionId);
        String jsonPermDTO = jsonMapper.writeValueAsString(Collections.singletonList(permDTO));

        mvc.perform(post("/roles/"+dummyRoleId+"/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPermDTO)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

    }

}
