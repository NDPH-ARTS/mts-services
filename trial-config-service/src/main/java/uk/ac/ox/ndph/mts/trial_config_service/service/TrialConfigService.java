package uk.ac.ox.ndph.mts.trial_config_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.trial_config_service.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.ResourceAlreadyExistsException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.RoleServiceException;
import uk.ac.ox.ndph.mts.trial_config_service.model.Trial;
import uk.ac.ox.ndph.mts.trial_config_service.model.TrialRepository;
import uk.ac.ox.ndph.mts.trial_config_service.model.TrialSite;
import uk.ac.ox.ndph.mts.trial_config_service.model.RoleDTO;
import uk.ac.ox.ndph.mts.trial_config_service.model.Person;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TrialConfigService {

    @Value("${role.service}")
    private String roleService;

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
     * minimal implementation without gateway or auth because trialconfigservice is now deprecated
     * (role-service will be called by git workflow on deploy)
     **/
    private void saveRoles(List<RoleDTO> roles) throws RoleServiceException {

        if (roles == null) {
            return;
        }

        try {
            for (RoleDTO role : roles) {

                webClient.post()
                        .uri(roleService + "/roles")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(Mono.just(role), RoleDTO.class)

                        .retrieve()// NB: auth
                        .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RoleServiceException(error))))
                        .bodyToMono(RoleDTO.class)
                        .block();

            }
        } catch (Exception e) {
            throw new RoleServiceException("Error connecting to role service");
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
