package uk.ac.ox.ndph.mts.site_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"uk.ac.ox.ndph.mts"})
public class SiteServiceApplication {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteServiceApplication.class);

    public static void main(String[] args) {
        LOGGER.info(Application.STARTUP.message());
        SpringApplication.run(SiteServiceApplication.class, args);
    }

}
