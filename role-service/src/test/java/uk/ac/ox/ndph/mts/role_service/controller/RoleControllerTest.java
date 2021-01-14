package uk.ac.ox.ndph.mts.role_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.model.RoleDTO;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;

import javax.ws.rs.core.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
    private ModelMapper modelMapper;


    @Test
    void givenValidRole_whenPost_thenReturnJson()
            throws Exception {

        String dummyName = "Dummy role name";
        RoleDTO role = new RoleDTO();
        role.setRoleName(dummyName);

        String jsonRole = jsonMapper.writeValueAsString(role);

        mvc.perform(post("/roles/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRole)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

    }

    @Test
    void givenInvalidRole_whenPost_thenReturn400()
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
    void whenConvertRoleEntityToDto_thenSameData() {

        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleName("test");

        RoleController c = new RoleController(roleRepo, new ModelMapper());
        Role roleEntity = c.convertDtoToEntity(roleDTO);

        assertEquals(roleEntity.getRoleName(), roleDTO.getRoleName());

    }


}
