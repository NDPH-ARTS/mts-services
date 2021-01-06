package uk.ac.ox.ndph.mts.trial_config_service.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.trial_config_service.config.GitRepo;
import uk.ac.ox.ndph.mts.trial_config_service.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.ResourceAlreadyExistsException;
import uk.ac.ox.ndph.mts.trial_config_service.model.Trial;
import uk.ac.ox.ndph.mts.trial_config_service.service.TrialConfigService;
import java.io.IOException;



@RestController
@RequestMapping("/trial-config")
public class TrialConfigController {

    private final TrialConfigService trialConfigService;
    private final WebClient webClient;
    private GitRepo gitRepo;

    //TODO(darrensmithson) - only temp until we decide where to get userId from
    String userId = "admin001";

    @Autowired
    public TrialConfigController(TrialConfigService trialConfigService, WebClient webClient, GitRepo gitRepo) {
        this.trialConfigService = trialConfigService;
        this.webClient = webClient;
        this.gitRepo = gitRepo;
    }

    public TrialConfigController(TrialConfigService trialConfigService, String baseUrl) {
        this.trialConfigService = trialConfigService;
        this.webClient = WebClient.create(baseUrl);
    }

    @GetMapping("/trial")
    public Trial saveTrialFromGit(
            @RequestParam
                    String trialConfig) throws InvalidConfigException, ResourceAlreadyExistsException {
        return trialConfigService.saveTrial(createTrialFromGitRepo(trialConfig), userId);
    }

    @PostMapping("/trial")
    public Trial saveTrialFromJson(
            @RequestBody
                    String trialConfigURL) throws InvalidConfigException, ResourceAlreadyExistsException {
        return trialConfigService.saveTrial(createTrialFromJsonData(trialConfigURL), userId);
    }

    protected Trial createTrialFromGitRepo(String fileName) {
        Trial trial;

        try {
            byte[] fileBytes = gitRepo.getTrialFile(fileName);
            ObjectMapper objMapper = new ObjectMapper();
            trial = objMapper.readValue(fileBytes, Trial.class);
        } catch (IOException ioException) {
            throw new InvalidConfigException(ioException.getMessage());
        } finally {
            gitRepo.destroy();
        }

        return trial;
    }

    protected Trial createTrialFromJsonData(String trialConfig) {
        Trial trial;

        try {
            ObjectMapper objMapper = new ObjectMapper();
            trial = objMapper.readValue(trialConfig, Trial.class);
        } catch (JsonProcessingException jpeEx) {
            throw new InvalidConfigException(jpeEx.getMessage());
        }
        return trial;
    }

    protected Mono<Trial> createTrialFromURL(String trialConfig) {
        Mono<Trial> response;

        try {
            response = webClient.get()
                .uri(trialConfig)
                .retrieve()
                .bodyToMono(Trial.class);
        } catch (WebClientException wceEx) {
            throw new InvalidConfigException(wceEx.getMessage());
        }

        return response;
    }

}
