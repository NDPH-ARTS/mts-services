package uk.ac.ox.ndph.mts.init_service.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.init_service.model.Trial;
import uk.ac.ox.ndph.mts.init_service.service.InitProgressService;
import uk.ac.ox.ndph.mts.init_service.service.PractitionerServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.RoleServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.SiteServiceInvoker;

import java.io.IOException;
import java.util.List;

@Component
public class Loader implements CommandLineRunner {

    private final PractitionerServiceInvoker practitionerServiceInvoker;
    private final RoleServiceInvoker roleServiceInvoker;
    private final SiteServiceInvoker siteServiceInvoker;
    private final Trial trialConfig;
    private final InitProgressService initProgressService;

    @Value("${delay-start:1}")
    private long delayStartInSeconds;

    @Autowired
    public Loader(Trial trialConfig, PractitionerServiceInvoker practitionerServiceInvoker,
                  RoleServiceInvoker roleServiceInvoker, SiteServiceInvoker siteServiceInvoker,
                  InitProgressService initProgressService) {
        this.trialConfig = trialConfig;
        this.practitionerServiceInvoker = practitionerServiceInvoker;
        this.roleServiceInvoker = roleServiceInvoker;
        this.siteServiceInvoker = siteServiceInvoker;
        this.initProgressService = initProgressService;
    }

    @Override
    public void run(String... args) throws InterruptedException, IOException {
        // Give the other services some time to come online.
        initProgressService.submitProgress(String.format("init service delaying for %d seconds.", delayStartInSeconds));
        Thread.sleep(delayStartInSeconds * 1000);

        try {
            initProgressService.submitProgress("getting roles from config.");
            var roles = trialConfig.getRoles();

            initProgressService.submitProgress("creating roles.");
            roleServiceInvoker.execute(roles);

            initProgressService.submitProgress("getting sites from config.");
            var sites = trialConfig.getSites();

            initProgressService.submitProgress("creating sites.");
            List<String> siteIds = siteServiceInvoker.execute(sites);

            initProgressService.submitProgress("getting persons from config.");
            var persons = trialConfig.getPersons();

            initProgressService.submitProgress("creating practitioner.");
            // This yuk but it is the assumption in story https://ndph-arts.atlassian.net/browse/ARTS-164
            String siteIdForUserRoles = siteIds.get(0);
            practitionerServiceInvoker.execute(persons, siteIdForUserRoles);
            initProgressService.submitProgress("***SUCCESS***");
        }
        catch (Exception ex) {
            initProgressService.submitProgress(ex.toString());
            initProgressService.submitProgress("***FAILURE***");
            throw ex;
        }
    }
}


