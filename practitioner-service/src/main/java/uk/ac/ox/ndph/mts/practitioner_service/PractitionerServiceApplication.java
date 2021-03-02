package uk.ac.ox.ndph.mts.practitioner_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"uk.ac.ox.ndph.mts"})
public class PractitionerServiceApplication {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PractitionerServiceApplication.class);

    public static void main(String[] args) {
        LOGGER.info(Application.STARTUP.message());
        SpringApplication.run(PractitionerServiceApplication.class, args);
    }

}
