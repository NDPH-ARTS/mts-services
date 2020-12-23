package com.microsoft.baseapp;

import com.microsoft.baseapp.config.IConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DemoAppTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IConfigService configService;

	@Test
	void TestGetSecret_WhenConfigServiceExists_CorrectValueReturned() throws Exception {
		// Arrange
		when(configService.getSecret()).thenReturn("123");

		// Act + Assert
		ResultActions secret = this.mockMvc.perform(get("/getsecret"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("123")));
	}

}
