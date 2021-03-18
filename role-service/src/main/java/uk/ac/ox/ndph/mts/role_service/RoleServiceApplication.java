package uk.ac.ox.ndph.mts.role_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class RoleServiceApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceApplication.class);

    public static void main(String[] args) {
        LOGGER.info(Application.STARTUP.message());
        SpringApplication.run(RoleServiceApplication.class, args);
    }
}
