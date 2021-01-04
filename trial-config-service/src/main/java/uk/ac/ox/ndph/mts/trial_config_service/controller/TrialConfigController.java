package uk.ac.ox.ndph.mts.trial_config_service.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.trial_config_service.config.GitRepo;
import uk.ac.ox.ndph.mts.trial_config_service.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.ResourceAlreadyExistsException;
import uk.ac.ox.ndph.mts.trial_config_service.model.Trial;
import uk.ac.ox.ndph.mts.trial_config_service.service.TrialConfigService;

import java.io.IOException;
import java.net.URISyntaxException;


@RestController
@RequestMapping("/trial-config")
public class TrialConfigController {

    private TrialConfigService trialConfigService;
    private WebClient webClient;
    private GitRepo gitRepo;

    //TODO - only temp until we decide where to get userId from
    String userId = "admin001";

    @Autowired
    public TrialConfigController(TrialConfigService trialConfigService, WebClient webClient, GitRepo gitRepo){
        this.trialConfigService = trialConfigService;
        this.webClient = webClient;
        this.gitRepo = gitRepo;
    }

    public TrialConfigController(TrialConfigService trialConfigService, String baseUrl){
        this.trialConfigService = trialConfigService;
        this.webClient = WebClient.create(baseUrl);
    }

    @GetMapping("/trial")
    Trial saveTrialFromGit(
            @RequestParam
                    String trialConfig) throws InvalidConfigException, ResourceAlreadyExistsException, IOException, GitAPIException, URISyntaxException {
        return trialConfigService.saveTrial(createTrialFromGitRepo(trialConfig), userId);
    }

    @PostMapping("/trial")
    Trial saveTrialFromJson(
            @RequestBody
                    String trialConfigURL) throws InvalidConfigException, ResourceAlreadyExistsException, IOException, GitAPIException, URISyntaxException {
        return trialConfigService.saveTrial(createTrialFromJsonData(trialConfigURL), userId);
    }

    protected Trial createTrialFromGitRepo(String fileName) throws IOException {
        byte[] fileBytes = gitRepo.getTrialFile(fileName);

        ObjectMapper objMapper = new ObjectMapper();
        return objMapper.readValue(fileBytes, Trial.class);
    }

    protected Trial createTrialFromJsonData(String trialConfig) throws JsonProcessingException {
        ObjectMapper objMapper = new ObjectMapper();
        return objMapper.readValue(trialConfig, Trial.class);
    }

    protected Mono<Trial> createTrialFromURL(String trialConfig) throws WebClientException {
        Mono<Trial> response = webClient.get()
                .uri(trialConfig)
                .retrieve()
                .bodyToMono(Trial.class);

        return response;
    }

}
