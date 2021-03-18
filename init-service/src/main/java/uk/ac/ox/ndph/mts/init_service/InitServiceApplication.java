package uk.ac.ox.ndph.mts.init_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class InitServiceApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitServiceApplication.class);

    public static void main(String[] args) {
        LOGGER.info(Application.STARTUP.message());
        SpringApplication.run(InitServiceApplication.class, args);
        LOGGER.info("Complete");
    }

}
