package uk.ac.ox.ndph.mts.trial_config_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("/test")
public class TestConfigController {

    @Value("${logging.level.uk.ac.ox.ndph.mts.trial_config_service}")
    String liquibaseEnabled;

    @RequestMapping("/message")
    String getMessage() {
        return this.liquibaseEnabled;
    }

}
