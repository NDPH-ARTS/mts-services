package uk.ac.ox.ndph.mts.saj_service.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ox.ndph.mts.saj_service.config.ConfigService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"server.error.include-message=always",
        "spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles({"local", "no-authZ"})
@AutoConfigureMockMvc
class MainControllerTests {

    @MockBean
    private ConfigService configService;

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser
    @Test
    void TestHelloEndpoint() throws Exception {
        // Act + Assert
        this.mockMvc.perform(get("/hello"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
