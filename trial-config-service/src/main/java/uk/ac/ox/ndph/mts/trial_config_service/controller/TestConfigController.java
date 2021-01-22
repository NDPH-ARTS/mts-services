package uk.ac.ox.ndph.mts.trial_config_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.trial_config_service.model.Trial;

import java.io.File;
import java.io.IOException;

@RefreshScope
@RestController
@RequestMapping("/test")
public class TestConfigController {

    private final Logger logger = LoggerFactory.getLogger(TestConfigController.class);

    @Value("${spring.cloud.config.uri}")
    private String configURI;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.cloud.config.profile}")
    private String profile;

    @Value("${spring.cloud.config.label}")
    private String label;


    @Autowired
    private ConfigurableApplicationContext context;

    private final WebClient webClient;

    public TestConfigController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/trial")
    public Trial saveTrialFromGit(
        @RequestParam
            String filename) throws IOException {

        return createTrial(filename, repoURL());
    }

    protected Trial createTrial(String trialConfig, String url) throws JsonProcessingException {
        Trial trial;

        try {
            trial = webClient.get()
                .uri(url + File.separator + trialConfig)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Trial.class)
                .block();
        } catch (WebClientException wceEx) {
            throw new InvalidConfigException(wceEx.getMessage());
        }

        return trial;
    }

    private String repoURL() {
        String path = configURI + File.separator + applicationName + File.separator + profile + File.separator + label;
        return path;
    }


}
