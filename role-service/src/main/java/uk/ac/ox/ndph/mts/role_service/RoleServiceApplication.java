package uk.ac.ox.ndph.mts.role_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"uk.ac.ox.ndph.mts"})
public class RoleServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoleServiceApplication.class, args);
    }
}
