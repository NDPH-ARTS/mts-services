package uk.ac.ox.ndph.mts.init_service.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.init_service.service.TrialService;
import uk.ac.ox.ndph.mts.init_service.service.PractitionerServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.RoleServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.SiteServiceInvoker;

@Component
public class Loader implements CommandLineRunner {

    @Autowired
    private TrialService trialService;

    @Autowired
    private PractitionerServiceInvoker practitionerServiceInvoker;

    @Autowired
    private RoleServiceInvoker roleServiceInvoker;

    @Autowired
    private SiteServiceInvoker siteServiceInvoker;

    @Override
    public void run(String... args) {
        roleServiceInvoker.execute(trialService.getTrial().getRoles());
        siteServiceInvoker.execute(trialService.getTrial().getSites());
        practitionerServiceInvoker.execute(trialService.getTrial().getPersons());
    }
}


