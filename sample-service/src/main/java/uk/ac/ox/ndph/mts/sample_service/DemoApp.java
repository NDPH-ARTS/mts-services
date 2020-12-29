package uk.ac.ox.ndph.mts.sample_service;

import uk.ac.ox.ndph.mts.sample_service.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class DemoApp {

	@Autowired
	public DemoApp(ConfigService configService)
	{
		this.myConfig = configService;
	}

	private ConfigService myConfig;

	@Value("${application.message:Not configured by a Spring Cloud Server}")
	private String message;

	@GetMapping("/getsecret")
	public String getsecret() {
		return this.myConfig.getSecret();
	}

	@GetMapping("/hello")
	public String hello() {
		return message;
	}
}
