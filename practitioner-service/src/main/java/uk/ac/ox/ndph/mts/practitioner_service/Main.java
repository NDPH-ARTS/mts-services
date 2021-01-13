package uk.ac.ox.ndph.mts.practitioner_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

/**
 *
 */
public class Main {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     *
     * @param args command line args
     */
    public static void main(String[] args) {
        LOGGER.info(Controllers.STARTUP.message());
        SpringApplication.run(PractitionerServiceController.class, args);
    }

}
