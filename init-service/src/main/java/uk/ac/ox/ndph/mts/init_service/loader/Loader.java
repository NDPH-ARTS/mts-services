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
import uk.ac.ox.ndph.mts.init_service.service.SiteServiceInvoker;

import java.util.List;

@Component
public class Loader implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(CommandLineRunner.class);

    private final PractitionerServiceInvoker practitionerServiceInvoker;
    private final RoleServiceInvoker roleServiceInvoker;
    private final SiteServiceInvoker siteServiceInvoker;
    private final Trial trialConfig;

    @Value("${delay-start:1}")
    private long delayStartInSeconds;

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
        // TODO: remove this temporary fix in this story: https://ndph-arts.atlassian.net/browse/ARTS-362
        // Give the other services some time to come online.
        logger.debug("Sleeping");
        Thread.sleep(delayStartInSeconds * 1000);

        logger.debug("Getting roles");
        roleServiceInvoker.execute(trialConfig.getRoles());
        logger.debug("Got roles.");

        logger.debug("Getting sites");
        List<String> siteIds = siteServiceInvoker.execute(trialConfig.getSites());
        logger.debug("Got sites");

        // This yuk but it is the assumption in story https://ndph-arts.atlassian.net/browse/ARTS-164
        String siteIdForUserRoles = siteIds.get(0);
        logger.debug("Getting practitioners");
        practitionerServiceInvoker.execute(trialConfig.getPersons(), siteIdForUserRoles);
        logger.debug("Got practitioners");
    }
}


