package uk.ac.ox.ndph.mts.trialconfigservice.controller;


import org.jboss.logging.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import uk.ac.ox.ndph.mts.trialconfigservice.config.WebConfig;
import uk.ac.ox.ndph.mts.trialconfigservice.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.trialconfigservice.exception.ResourceAlreadyExistsException;
import uk.ac.ox.ndph.mts.trialconfigservice.model.Trial;
import uk.ac.ox.ndph.mts.trialconfigservice.model.TrialRepository;
import uk.ac.ox.ndph.mts.trialconfigservice.service.TrialConfigService;


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
                    String trialConfigURL,
            @AuthenticationPrincipal Jwt jwt) throws InvalidConfigException, ResourceAlreadyExistsException {

        return trialConfigService.saveTrial(createTrial(trialConfigURL), jwt.getClaimAsString(USER_IDENTITY_IN_TOKEN));

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
