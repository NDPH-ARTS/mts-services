package uk.ac.ox.ndph.mts.trial_config_service.service;

import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.trial_config_service.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.ResourceAlreadyExistsException;
import uk.ac.ox.ndph.mts.trial_config_service.model.Person;
import uk.ac.ox.ndph.mts.trial_config_service.model.Role;
import uk.ac.ox.ndph.mts.trial_config_service.model.Trial;
import uk.ac.ox.ndph.mts.trial_config_service.model.TrialRepository;
import uk.ac.ox.ndph.mts.trial_config_service.model.TrialSite;
import uk.ac.ox.ndph.mts.trial_config_service.model.SiteTypes;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class TrialConfigService {

    private final TrialRepository trialRepository;

    public TrialConfigService(TrialRepository trialRepository) {
        this.trialRepository = trialRepository;
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

        addBootstrapUser(trialSite.get(), userId);
        trial.setStatus(Trial.Status.IN_CONFIGURATION);
        addAuditData(trial, userId);

        return trialRepository.save(trial);
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

        for (Role role : trial.getRoles()) {
            role.setModifiedTime(LocalDateTime.now());
            role.setModifiedBy(userId);
            role.setTrial(trial);
        }

        for (SiteTypes siteTypes : trial.getSiteTypes()) {
            siteTypes.setModifiedTime(LocalDateTime.now());
            siteTypes.setModifiedBy(userId);
            siteTypes.setTrial(trial);
        }
    }

}
