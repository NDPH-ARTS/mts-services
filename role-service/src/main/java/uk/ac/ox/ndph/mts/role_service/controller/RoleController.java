package uk.ac.ox.ndph.mts.role_service.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/roles")
public class RoleController {


    @GetMapping("/")
    public String hello(){
        return "hello world";
    }


}
