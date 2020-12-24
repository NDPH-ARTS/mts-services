package uk.ac.ox.ndph.arts.practitiner_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {

        LOGGER.info("Staring practitioner service...");
        SpringApplication.run(PractitionerServiceApp.class, args);
    }

}
