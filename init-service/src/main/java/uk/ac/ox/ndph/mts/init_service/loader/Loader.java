package uk.ac.ox.ndph.mts.init_service.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.init_service.model.Trial;
import uk.ac.ox.ndph.mts.init_service.service.PractitionerServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.RoleServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.ServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.SiteServiceInvoker;

import java.util.List;

@Component
public class Loader implements CommandLineRunner {

    private final PractitionerServiceInvoker practitionerServiceInvoker;
    private final RoleServiceInvoker roleServiceInvoker;
    private final SiteServiceInvoker siteServiceInvoker;
    private final Trial trialConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(Loader.class);

    @Value("${delay-start:1}")
    private long delayStartInSeconds;

    @Value("${INIT_STORAGE_ACCOUNT_CONNECTION_STRING}")
    private String initStorageConnectionString;

    @Autowired
    public Loader(Trial trialConfig, PractitionerServiceInvoker practitionerServiceInvoker,
                  RoleServiceInvoker roleServiceInvoker, SiteServiceInvoker siteServiceInvoker) {
        this.trialConfig = trialConfig;
        this.practitionerServiceInvoker = practitionerServiceInvoker;
        this.roleServiceInvoker = roleServiceInvoker;
        this.siteServiceInvoker = siteServiceInvoker;
    }

    @Override
    public void run(String... args) throws InterruptedException {
        // Give the other services some time to come online.
        Thread.sleep(delayStartInSeconds * 1000);

        LOGGER.info("init connection string=" + initStorageConnectionString);

        roleServiceInvoker.execute(trialConfig.getRoles());
        List<String> siteIds = siteServiceInvoker.execute(trialConfig.getSites());
        // This yuk but it is the assumption in story https://ndph-arts.atlassian.net/browse/ARTS-164
        String siteIdForUserRoles = siteIds.get(0);
        practitionerServiceInvoker.execute(trialConfig.getPersons(), siteIdForUserRoles);
    }
}


