package uk.ac.ox.ndph.mts.handoff_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class of this service.
 */
@SpringBootApplication(scanBasePackages = {"uk.ac.ox.ndph.mts"})
public class HandoffServiceApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(HandoffServiceApplication.class);

    /**
     * The entry point for this class.
     * @param args command line args
     */
    public static void main(String[] args) {

        LOGGER.info("Starting handoff service app...");
        SpringApplication.run(HandoffServiceApplication.class, args);

    }

}
