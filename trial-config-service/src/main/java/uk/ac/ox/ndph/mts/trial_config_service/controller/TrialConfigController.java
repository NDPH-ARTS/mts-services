package uk.ac.ox.ndph.mts.trial_config_service.controller;


import org.jboss.logging.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import uk.ac.ox.ndph.mts.trial_config_service.config.WebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.trial_config_service.model.Trial;
import uk.ac.ox.ndph.mts.trial_config_service.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.ResourceAlreadyExistsException;
import uk.ac.ox.ndph.mts.trial_config_service.service.TrialConfigService;


@RestController
@RequestMapping("/trial-config")
public class TrialConfigController {

    private TrialConfigService trialConfigService;

    private WebClient webClient;


    private static final String USER_IDENTITY_IN_TOKEN = "oid";

    @Autowired
    public TrialConfigController(TrialConfigService trialConfigService, WebClient webClient){
        this.trialConfigService = trialConfigService;
        this.webClient = webClient;
    }

    public TrialConfigController(TrialConfigService trialConfigService, String baseUrl){
        this.trialConfigService = trialConfigService;
        this.webClient = WebClient.create(baseUrl);
    }

    @PostMapping("/trial")
    Trial saveTrial(
            @RequestBody
                    String trialConfigURL) throws InvalidConfigException, ResourceAlreadyExistsException {

        return trialConfigService.saveTrial(createTrial(trialConfigURL).block(), jwt.getClaimAsString(USER_IDENTITY_IN_TOKEN));

    }

    protected Mono<Trial> createTrial(String trialConfigURL) throws WebClientException {

        Mono<Trial> response = webClient.get()
                .uri(trialConfigURL)
                .retrieve()
                .bodyToMono(Trial.class);
        return response;
    }



}
