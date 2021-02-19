package uk.ac.ox.ndph.mts.roleserviceclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RoleServiceClientApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceClientApplication.class);

    public static void main(String[] args) {
        LOGGER.info("Starting role-service-client...");
        SpringApplication.run(RoleServiceClientApplication.class, args);
    }
}
