package uk.ac.ox.ndph.mts.saj_service.controller;

import uk.ac.ox.ndph.mts.sample_service.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The application
 */
@RestController
public class MainController {

    private ConfigService myConfig;

    @Value("${application.message:Not configured by a Spring Cloud Server}")
    private String message;

    /**
     * The constructor for this class.
     *
     * @param configService a config service
     */
    @Autowired
    public MainController(ConfigService configService) {
        this.myConfig = configService;
    }

    @GetMapping("/hello")
    public String getHello() {
        return "Hello";
    }

}
