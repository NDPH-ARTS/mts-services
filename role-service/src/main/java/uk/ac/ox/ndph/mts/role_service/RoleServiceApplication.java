package uk.ac.ox.ndph.mts.role_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Role Service Application
 */
@SpringBootApplication
public class RoleServiceApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceApplication.class);

    /**
     * Main method for RoleServiceApplication
     * @param args args for RoleServiceApplication
     */
    public static void main(String[] args) {
        LOGGER.info(Application.STARTUP.message());
        SpringApplication.run(RoleServiceApplication.class, args);
    }
}
