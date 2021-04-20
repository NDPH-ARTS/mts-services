package uk.ac.ox.ndph.mts.hack_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The application
 */
@RestController
public class TheHackMainController {

    @Value("${application.message:Not configured by a Spring Cloud Server}")
    private String message;

    /**
     * An additional endpoint.
     *
     * @return a message as a string.
     */
    @GetMapping("/hello")
    public String hello() {
        return message;
    }
}
