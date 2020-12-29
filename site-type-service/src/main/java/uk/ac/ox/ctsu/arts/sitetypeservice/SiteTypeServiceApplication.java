package uk.ac.ox.ctsu.arts.sitetypeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SiteTypeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SiteTypeServiceApplication.class, args);
    }
}
