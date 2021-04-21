package uk.ac.ox.ndph.mts.handoff_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * The application
 */
@RestController
public class MainController {

    @Value("${mts.handoff.message:Not configured by a Spring Cloud Server}")
    private String message;

    /**
     * The constructor for this class.
     */
    @Autowired
    public MainController() { }

    @PreAuthorize("@authorisationService.authorise('view-hello')")
    @GetMapping("/hello")
    public String hello() {
        return message;
    }
}
