package uk.ac.ox.ndph.mts.trial_config_service.service;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.trial_config_service.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.ResourceAlreadyExistsException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.RoleServiceException;
import uk.ac.ox.ndph.mts.trial_config_service.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TrialConfigService {

    private final TrialRepository trialRepository;
    private final WebClient webClient;

    public TrialConfigService(TrialRepository trialRepository, WebClient webClient) {
        this.trialRepository = trialRepository;
        this.webClient = webClient;
    }

    public Trial saveTrial(Trial trial, String userId) throws InvalidConfigException, ResourceAlreadyExistsException {

        TrialSite.SiteType rootNodeType = TrialSite.SiteType.CCO; // this is the assumption for now

        if (trialRepository.existsById(trial.getId())) {
            throw new ResourceAlreadyExistsException();
        }

        Optional<TrialSite> trialSite = trial.getTrialSites().stream()
                .filter(site -> Objects.nonNull(site.getSiteType()) && site.getSiteType().equals(rootNodeType))
                .findFirst();

        if (trialSite.isEmpty()) {
            throw new InvalidConfigException();
        }

        saveRoles(trial.getRoles());

        addBootstrapUser(trialSite.get(), userId);
        trial.setStatus(Trial.Status.IN_CONFIGURATION);
        addAuditData(trial, userId);
        return trialRepository.save(trial);
    }

    /**
     * minimal implementation without gateway or auth because trialconfigservice is now deprecated - role-service will be called by git workflow on deploy
     **/
    private void saveRoles(List<Role> roles) throws RoleServiceException {

        String roleServiceURI = "http://localhost:82/roles";// NB: gateway
        if (roles == null) {
            return;
        }

        for (Role role : roles) {

            webClient.post()
                    .uri(roleServiceURI)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(role), Role.class)

                    .retrieve()// NB: auth
                    .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(new RoleServiceException(error))))
                    .bodyToMono(Role.class)
                    .block();

        }


    }

    private void addBootstrapUser(TrialSite trialSite, String userId) { // this will change once we have Roles
        Person bootstrapUser = new Person(userId, LocalDateTime.now(), userId);
        trialSite.setUser(bootstrapUser);
        bootstrapUser.setTrialSite(trialSite);
    }

    private void addAuditData(Trial trial, String userId) {
        trial.setModifiedTime(LocalDateTime.now());
        trial.setModifiedBy(userId);

        for (TrialSite trialSite : trial.getTrialSites()) {
            trialSite.setModifiedTime(LocalDateTime.now());
            trialSite.setModifiedBy(userId);
            trialSite.setTrial(trial);
        }

    }

}
