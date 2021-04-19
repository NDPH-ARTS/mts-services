package uk.ac.ox.ndph.mts.handoff_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The application
 */
@RestController
public class MainController {

    /**
     * The constructor for this class.
     */
    @Autowired
    public MainController() {
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
