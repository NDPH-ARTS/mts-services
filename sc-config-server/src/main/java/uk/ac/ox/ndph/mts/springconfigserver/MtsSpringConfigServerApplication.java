package uk.ac.ox.ndph.mts.springconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class MtsSpringConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MtsSpringConfigServerApplication.class, args);
    }

}
