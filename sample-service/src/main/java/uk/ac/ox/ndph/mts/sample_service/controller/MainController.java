package uk.ac.ox.ndph.mts.sample_service.controller;

import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
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

    /**
     * The endpoint to get the secret.
     *
     * @return the secret as a string.
     */
    @PreAuthorize("@authorisationService.authorise('stubPermission')") //NOSONAR
    @GetMapping("/getsecret")
    public String getsecret() {
        return this.myConfig.getSecret();
    }

    /**
     * An additional endpoint.
     *
     * @return a message as a string.
     */
    @PreAuthorize("@authorisationService.authorise('stubPermission')") //NOSONAR
    @GetMapping("/hello")
    public String hello() {
        return message;
    }
}
