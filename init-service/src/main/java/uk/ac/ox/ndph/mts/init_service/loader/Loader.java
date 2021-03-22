package uk.ac.ox.ndph.mts.init_service.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.init_service.model.Trial;
import uk.ac.ox.ndph.mts.init_service.service.PractitionerServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.SiteServiceInvoker;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;


import java.util.List;

@Component
public class Loader implements CommandLineRunner {

    private final PractitionerServiceInvoker practitionerServiceInvoker;
    private final RoleServiceClient roleServiceClient;
    private final SiteServiceInvoker siteServiceInvoker;
    private final Trial trialConfig;

    @Value("${delay-start:1}")
    private long delayStartInSeconds;

    @Autowired
    public Loader(final Trial trialConfig,
                  final PractitionerServiceInvoker practitionerServiceInvoker,
                  final RoleServiceClient roleServiceClient,
                  final SiteServiceInvoker siteServiceInvoker) {
        this.trialConfig = trialConfig;
        this.practitionerServiceInvoker = practitionerServiceInvoker;
        this.roleServiceClient = roleServiceClient;
        this.siteServiceInvoker = siteServiceInvoker;
    }


    @Override
    public void run(String... args) throws InterruptedException {
        // TODO: remove this temporary fix in this story: https://ndph-arts.atlassian.net/browse/ARTS-362
        // Give the other services some time to come online.
        Thread.sleep(delayStartInSeconds * 1000);

        roleServiceClient.createMany(trialConfig.getRoles(), roleServiceClient.noAuth());
        List<String> siteIds = siteServiceInvoker.execute(trialConfig.getSites());
        // This yuk but it is the assumption in story https://ndph-arts.atlassian.net/browse/ARTS-164
        String siteIdForUserRoles = siteIds.get(0);
        practitionerServiceInvoker.execute(trialConfig.getPersons(), siteIdForUserRoles);
    }
}


