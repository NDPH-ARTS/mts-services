package uk.ac.ox.ndph.mts.handoff_service.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "spring.cloud.config.discovery.enabled = false" , "spring.cloud.config.enabled=false", "server.error.include-message=always", "spring.main.allow-bean-definition-overriding=true" })
@AutoConfigureMockMvc
class MainControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void TestHelloEndpoint() throws Exception {
        // Act + Assert
        this.mockMvc.perform(get("/hello"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
