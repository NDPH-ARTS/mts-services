package uk.ac.ox.ndph.mts.trial_config_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.ResourceAlreadyExistsException;
import uk.ac.ox.ndph.mts.trial_config_service.model.Trial;
import uk.ac.ox.ndph.mts.trial_config_service.service.TrialConfigService;

@RefreshScope
@RestController
@RequestMapping("/trial-config")
public class TrialConfigController {

    @Value("${spring.cloud.config.uri}")
    private String configURI;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.cloud.config.profile}")
    private String profile;

    @Value("${spring.cloud.config.label}")
    private String label;

    private final TrialConfigService trialConfigService;
    private final WebClient webClient;

    //TODO(darrensmithson) - only temp until we decide where to get userId from
    String userId = "admin001";

    @Autowired
    public TrialConfigController(TrialConfigService trialConfigService, WebClient webClient) {
        this.trialConfigService = trialConfigService;
        this.webClient = webClient;
    }

    //TODO(darrensmithson) - we are using a GET and POST for similar functionlaity to accomodate
    // early testing needs, once we are closer to final developmemnt we should ensure only 1 POST is used.
    @GetMapping("/trial")
    public Trial saveTrialFromConfigServer(
            @RequestParam
                    String filename)
        throws InvalidConfigException, ResourceAlreadyExistsException {
        return trialConfigService.saveTrial(createTrial(filename, getRepoURL()), userId);
    }

    @PostMapping("/trial")
    public Trial saveTrialFromJson(
            @RequestBody
                    String jsonData) throws InvalidConfigException, ResourceAlreadyExistsException {
        return trialConfigService.saveTrial(createTrial(jsonData), userId);
    }

    protected Trial createTrial(String jsonData) {
        Trial trial;

        try {
            ObjectMapper objMapper = new ObjectMapper();
            trial = objMapper.readValue(jsonData, Trial.class);
        } catch (JsonProcessingException jpeEx) {
            throw new InvalidConfigException(jpeEx.getMessage());
        }
        return trial;
    }

    protected Trial createTrial(String trialConfig, String url) {
        Trial trial;

        try {
            trial = webClient.get()
                .uri(url + "/" + trialConfig)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Trial.class)
                .block();
        } catch (WebClientException wceEx) {
            throw new InvalidConfigException(wceEx.getMessage());
        }

        return trial;
    }

    private String getRepoURL() {
        return configURI + "/" + applicationName + "/" + profile + "/" + label;
    }

}
