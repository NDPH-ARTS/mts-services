package uk.ac.ox.ndph.mts.role_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import uk.ac.ox.ndph.mts.role_service.model.Role;
import uk.ac.ox.ndph.mts.role_service.model.RoleRepository;

import javax.ws.rs.core.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(RoleController.class)
class RoleControllerTest {


    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RoleRepository service;


    @Test
    public void givenValidRole_whenPost_thenReturnJson()
            throws Exception {

        String dummyName = "Dummy role name";
        Role role = new Role();
        role.setRoleName(dummyName);

        String jsonRole = mapper.writeValueAsString(role);

        mvc.perform(post("/roles/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRole)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

    }

    @Test
    public void givenInvalidRole_whenPost_thenReturn400()
            throws Exception {


        Role role = new Role(); // no name

        String jsonRole = mapper.writeValueAsString(role);

        mvc.perform(post("/roles/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRole)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }


}
