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

    private Environment env;

    private ConfigService myConfig;

    private final static String managedIdentityProperty = "MANAGED_IDENTITY";

    @Value("${application.message:Not configured by a Spring Cloud Server}")
    private String message;

    /**
     * The constructor for this class.
     *
     * @param configService a config service
     */
    @Autowired
    public MainController(ConfigService configService, Environment env) {
        this.myConfig = configService;
        this.env = env;
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
    @PreAuthorize("@authenticationService.authenticate(env.getProperty(managedIdentityProperty)) or @authorisationService.authorise('stubPermission')") //NOSONAR
    @GetMapping("/hello")
    public String hello() {
        return message;
    }
}
