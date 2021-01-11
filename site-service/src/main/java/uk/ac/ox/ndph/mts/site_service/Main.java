package uk.ac.ox.ndph.mts.site_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

/**
 *
 */
public class Main {
    // TODO: take static strings from resource file
    private static final String STARTUP_LOG = "Staring site service...";
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     *
     * @param args command line args
     */
    public static void main(String[] args) {

        LOGGER.info(STARTUP_LOG);
        SpringApplication.run(SiteServiceApp.class, args);
    }

}
