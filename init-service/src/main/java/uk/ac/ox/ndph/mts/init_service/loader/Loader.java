package uk.ac.ox.ndph.mts.init_service.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.init_service.service.TrialService;
import uk.ac.ox.ndph.mts.init_service.service.PractitionerServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.RoleServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.SiteServiceInvoker;

import java.util.List;

@Component
public class Loader implements CommandLineRunner {

    private TrialService trialService;

    private PractitionerServiceInvoker practitionerServiceInvoker;

    private RoleServiceInvoker roleServiceInvoker;

    private SiteServiceInvoker siteServiceInvoker;

    @Autowired
    public Loader(TrialService trialService, PractitionerServiceInvoker practitionerServiceInvoker,
                  RoleServiceInvoker roleServiceInvoker, SiteServiceInvoker siteServiceInvoker) {
        this.trialService = trialService;
        this.practitionerServiceInvoker = practitionerServiceInvoker;
        this.roleServiceInvoker = roleServiceInvoker;
        this.siteServiceInvoker = siteServiceInvoker;
    }


    @Override
    public void run(String... args) throws Exception {
        roleServiceInvoker.execute(trialService.getTrial().getRoles());
        List<String> siteIds = siteServiceInvoker.execute(trialService.getTrial().getSites());
        String siteIdForUserRoles = siteIds.get(0); // This yuk but it is the assumption
                                                    // in story https://ndph-arts.atlassian.net/browse/ARTS-164
        practitionerServiceInvoker.execute(trialService.getTrial().getPersons(), siteIdForUserRoles);
    }
}


