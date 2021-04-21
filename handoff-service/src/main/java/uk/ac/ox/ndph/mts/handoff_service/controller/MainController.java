package uk.ac.ox.ndph.mts.handoff_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ox.ndph.mts.handoff_service.config.ConfigService;

/**
 * The application
 */
@RestController
public class MainController {
    private ConfigService myConfig;

    /**
     * The constructor for this class.
     *
     * @param configService a config service
     */
    @Autowired
    public MainController(ConfigService configService) {
        this.myConfig = configService;
    }

    @PreAuthorize("@authorisationService.authorise('view-hello')")
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
