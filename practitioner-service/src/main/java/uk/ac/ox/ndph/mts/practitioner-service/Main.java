package uk.ac.ox.ndph.mts.practitioner_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

public class Main {
    // TODO: take static strings from resource file user story #...
    private static final String STARTUP_LOG = "Staring practitioner service...";
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {

        LOGGER.info(STARTUP_LOG);
        SpringApplication.run(PractitionerServiceApp.class, args);
    }

}
