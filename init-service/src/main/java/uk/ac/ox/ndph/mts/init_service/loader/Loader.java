package uk.ac.ox.ndph.mts.init_service.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.init_service.model.Trial;
import uk.ac.ox.ndph.mts.init_service.service.PractitionerServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.RoleServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.SiteServiceInvoker;
//import uk.ac.ox.ndph.mts.init_service.service.TrialService;

import java.util.List;

@Component
public class Loader implements CommandLineRunner {

//    private final TrialService trialService;
    private final PractitionerServiceInvoker practitionerServiceInvoker;
    private final RoleServiceInvoker roleServiceInvoker;
    private final SiteServiceInvoker siteServiceInvoker;
    private final Trial trialConfig;

    @Autowired
    public Loader(Trial trialConfig, PractitionerServiceInvoker practitionerServiceInvoker,
                  RoleServiceInvoker roleServiceInvoker, SiteServiceInvoker siteServiceInvoker) {
        this.trialConfig = trialConfig;
        this.practitionerServiceInvoker = practitionerServiceInvoker;
        this.roleServiceInvoker = roleServiceInvoker;
        this.siteServiceInvoker = siteServiceInvoker;
    }


    @Override
    public void run(String... args) {
        roleServiceInvoker.execute(trialConfig.getRoles());
        List<String> siteIds = siteServiceInvoker.execute(trialConfig.getSites());
        String siteIdForUserRoles = siteIds.get(0); // This yuk but it is the assumption
                                                    // in story https://ndph-arts.atlassian.net/browse/ARTS-164
        practitionerServiceInvoker.execute(trialConfig.getPersons(), siteIdForUserRoles);
    }
}


