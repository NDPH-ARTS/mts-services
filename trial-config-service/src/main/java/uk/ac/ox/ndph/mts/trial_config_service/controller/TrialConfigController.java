package uk.ac.ox.ndph.mts.trial_config_service.controller;


import org.jboss.logging.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import uk.ac.ox.ndph.mts.trial_config_service.config.WebConfig;
import uk.ac.ox.ndph.mts.trial_config_service.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.ResourceAlreadyExistsException;
import uk.ac.ox.ndph.mts.trial_config_service.model.Trial;
import uk.ac.ox.ndph.mts.trial_config_service.model.TrialRepository;
import uk.ac.ox.ndph.mts.trial_config_service.service.TrialConfigService;


@RestController
@RequestMapping("/trial-config")
public class TrialConfigController {

    private TrialConfigService trialConfigService;

    private TrialRepository trialRepository;

    private WebConfig webConfig;


    private static final String USER_IDENTITY_IN_TOKEN = "oid";

    public TrialConfigController(TrialConfigService trialConfigService, TrialRepository trialRepository, WebConfig webConfig){
        this.trialConfigService = trialConfigService;
        this.trialRepository = trialRepository;
        this.webConfig = webConfig;
    }

    @PostMapping("/trial")
    Trial saveTrial(
            @RequestBody
                    String trialConfigURL) throws InvalidConfigException, ResourceAlreadyExistsException {

        return trialConfigService.saveTrial(createTrial(trialConfigURL));

    }

    protected Trial createTrial(String trialConfigURL) throws InvalidConfigException {
        try {
            RestTemplate restTemplate = webConfig.restTemplate();
            ResponseEntity<Trial> response = restTemplate.getForEntity(trialConfigURL, Trial.class);
            return response.getBody();
        } catch(Exception e) {
            Logger.getLogger(TrialConfigController.class).error("Failed to parse trial config!", e);
            throw new InvalidConfigException("Failed to parse trial config: " + e.getMessage());
        }
    }
}
