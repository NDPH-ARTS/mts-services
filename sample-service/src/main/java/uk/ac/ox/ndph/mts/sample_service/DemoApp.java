package uk.ac.ox.ndph.mts.sample_service;

import uk.ac.ox.ndph.mts.sample_service.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The application
 */
@RestController
@SpringBootApplication
public class DemoApp {

    /**
     * The constructor for this class.
     * @param configService a config service
     */
    @Autowired
    public DemoApp(ConfigService configService) {
        this.myConfig = configService;
    }

    private ConfigService myConfig;

    @Value("${application.message:Not configured by a Spring Cloud Server}")
    private String message;

    /**
     * The endpoint to get the secret.
     * @return the secret as a string.
     */
    @GetMapping("/getsecret")
    public String getsecret() {
        return this.myConfig.getSecret();
    }

    /**
     * An additional endpoint.
     * @return a message as a string.
     */
    @GetMapping("/hello")
    public String hello() {
        return message;
    }
}
