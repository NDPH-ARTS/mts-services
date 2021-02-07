package uk.ac.ox.ndph.mts.role_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import uk.ac.ox.ndph.mts.role_service.model.Permission;
import uk.ac.ox.ndph.mts.role_service.model.PermissionRepository;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(properties = {"spring.cloud.config.discovery.enabled = false", "spring.cloud.config.enabled=false", "spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureMockMvc
class PermissionControllerTest {


    @Autowired
    private MockMvc mvc;

    @MockBean
    private PermissionRepository permissionRepo;

    private String URI_PERMISSIONS = "/permissions";

    @Test
    void whenGetPaged_thenReceiveSuccess()
            throws Exception {
        Permission dummyPermission = new Permission();
        String dummyId="dummy-permission";
        dummyPermission.setId(dummyId);

        when(permissionRepo.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl(Collections.singletonList(dummyPermission)));
        mvc.perform(get(URI_PERMISSIONS + "?page=0&size=10"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(dummyId)));

    }

   

}
