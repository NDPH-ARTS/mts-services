package uk.ac.ox.ndph.mts.sample_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

/**
 * The main class of this service.
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * The entry point for this class.
     * @param args command line args
     */
    public static void main(String[] args) {

        LOGGER.info("Staring base app...");
        SpringApplication.run(DemoApp.class, args);

    }

}
