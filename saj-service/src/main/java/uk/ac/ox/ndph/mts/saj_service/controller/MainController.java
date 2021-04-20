package uk.ac.ox.ndph.mts.saj_service.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The application
 */
@RestController
public class MainController {

    @GetMapping("/hello")
    public String getHello() {
        return "Hello";
    }

}
