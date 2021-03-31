package uk.ac.ox.ndph.mts.role_service.controller;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import uk.ac.ox.ndph.mts.role_service.controller.dtos.PermissionDTO;
import uk.ac.ox.ndph.mts.role_service.controller.dtos.RoleDTO;
import uk.ac.ox.ndph.mts.role_service.model.Permission;
import uk.ac.ox.ndph.mts.role_service.model.PermissionRepository;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;
import uk.ac.ox.ndph.mts.role_service.service.RoleService;

@SpringBootTest(properties = {"spring.liquibase.enabled=false", "spring.cloud.config.discovery.enabled = false", 
                                "spring.cloud.config.enabled=false", "server.error.include-message=always", "spring.main.allow-bean-definition-overriding=true",
                                "jdbc.url=jdbc:h2:mem:testdb", "jdbc.driver=org.h2.Driver", "role.service.uri=d", "site.service.uri=f", "practitioner.service.uri=g"})
@AutoConfigureMockMvc
@ActiveProfiles({"no-authZ"})
@AutoConfigureTestDatabase
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

    private String URI_ROLES = "/roles";
    private String URI_ROLE = "/roles/%s";
    private String URI_PERMISSIONS_FOR_ROLE = "/roles/%s/permissions";

    @WithMockUser
    @Test
    void whenPostValidRole_thenReturnSuccess() throws Exception {

        String dummyName = "Dummy role name";
        RoleDTO role = new RoleDTO();
        role.setId(dummyName);

        String jsonRole = jsonMapper.writeValueAsString(role);

        mvc.perform(post(URI_ROLES).contentType(MediaType.APPLICATION_JSON).content(jsonRole)
                .accept(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk());

    }

    @WithMockUser
    @Test
    void whenPostInvalidRole_thenReturn400() throws Exception {

        RoleDTO role = new RoleDTO(); // no name

        String jsonRole = jsonMapper.writeValueAsString(role);

        mvc.perform(post(URI_ROLES).contentType(MediaType.APPLICATION_JSON).content(jsonRole)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().is4xxClientError());

    }

    @WithMockUser
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
        assertTrue(roleEntity.getPermissions().stream().anyMatch(perm -> perm.getId().equals(permissionDTO.getId())));

    }

    @WithMockUser
    @Test
    void whenGetRolesPaged_thenReceiveSuccess() throws Exception {

        mvc.perform(get(URI_ROLES + "?page=0&size=10")).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk());
    }


    @WithMockUser
    @Test
    void whenGetOneRole_thenReceiveSuccess() throws Exception {

        Role dummyRole = new Role();
        String dummyId = "dummy-role";
        dummyRole.setId(dummyId);

        when(roleRepo.findById(dummyId)).thenReturn(Optional.of(dummyRole));
        mvc.perform(get(String.format(URI_ROLE, dummyId))).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andExpect(content().string(containsString(dummyId)));
    }

    @WithMockUser
    @Test
    void whenGetNonExistentRole_thenReceive404() throws Exception {

        mvc.perform(get(String.format(URI_ROLE, "/nonexistentrole"))).andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }

    @WithMockUser
    @Test
    void whenGetRole_thenAlsoReceivePermissionsForRole() throws Exception {

        Role dummyRole = new Role();
        String dummyRoleId = "dummy-role";
        dummyRole.setId(dummyRoleId);
        Permission dummyPermission = new Permission();
        String dummyPermissionId = "dummy-permission";
        dummyPermission.setId("dummy-permission");
        dummyRole.setPermissions(Collections.singletonList(dummyPermission));

        when(roleRepo.findById(dummyRoleId)).thenReturn(Optional.of(dummyRole));
        mvc.perform(get(String.format(URI_ROLE, dummyRoleId))).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andExpect(content().string(containsString(dummyRoleId)))
                .andExpect(content().string(containsString(dummyPermissionId)));
    }

    @WithMockUser
    @Test
    void whenPostValidPermissionForRole_thenReceiveSuccess() throws Exception {

        String dummyRoleId = "dummy-role";
        String dummyPermissionId = "dummy-permission";

        PermissionDTO permDTO = new PermissionDTO();
        permDTO.setId(dummyPermissionId);
        String jsonPermDTO = jsonMapper.writeValueAsString(Collections.singletonList(permDTO));

        String uri = String.format(URI_PERMISSIONS_FOR_ROLE, dummyRoleId);
        mvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(jsonPermDTO)
                .accept(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk());

    }

    @WithMockUser
    @Test
    void whenGetMultipleRolesById_thenReceiveMultipleRolesAndPermissionsJson() throws Exception {

        Role dummyRole1 = new Role();
        String roleId1 = "foo";
        dummyRole1.setId(roleId1);
        Role dummyRole2 = new Role();
        String roleId2 = "bar";
        dummyRole2.setId(roleId2);
        Permission dummyPermission = new Permission();
        String permId = "baz";
        dummyPermission.setId(permId);
        dummyRole2.setPermissions(Collections.singletonList(dummyPermission));

        when(roleRepo.findAllById(Arrays.asList(roleId1, roleId2)))
                .thenReturn(Arrays.asList(dummyRole1, dummyRole2));

        mvc.perform(get(URI_ROLES + "?ids=" + roleId1 + "," + roleId2))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(roleId1))
                .andExpect(jsonPath("$[1].id").value(roleId2))
                .andExpect(jsonPath("$[1].permissions[0].id").value(permId));


    }
    
    // @TestConfiguration
    // static class TestAuthorisationConfigurationProvider {

    //     @Bean
    //     @Primary
    //     public AuthorisationService authorisationService() {
    //         var mockService = Mockito.mock(AuthorisationService.class);
    //         Mockito.when(mockService.authorise(anyString())).thenReturn(true);
    //         Mockito.when(mockService.authorise(anyString(), anyString())).thenReturn(true);
    //         Mockito.when(mockService.authorise(anyString(), anyList())).thenReturn(true);
    //         //Add more mocks if needed
    //         return mockService;
    //     }
    
    //     @Bean
    //     @Primary
    //     public AADAppRoleStatelessAuthenticationFilter filter() {
    //         return new AADAppRoleStatelessAuthenticationFilter(Mockito.mock(UserPrincipalManager.class));
    //     }
    // }
}
