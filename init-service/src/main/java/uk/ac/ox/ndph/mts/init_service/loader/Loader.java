package uk.ac.ox.ndph.mts.init_service.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.init_service.service.JsonService;
import uk.ac.ox.ndph.mts.init_service.service.PractitionerServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.RoleServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.SiteServiceInvoker;

@Component
public class Loader implements CommandLineRunner {

    @Autowired
    private JsonService jsonService;

    @Autowired
    private PractitionerServiceInvoker practitionerServiceInvoker;

    @Autowired
    private RoleServiceInvoker roleServiceInvoker;

    @Autowired
    private SiteServiceInvoker siteServiceInvoker;

    @Override
    public void run(String... args) {
        roleServiceInvoker.execute(jsonService.getTrial().getRoles());
        siteServiceInvoker.execute(jsonService.getTrial().getSites());
        practitionerServiceInvoker.execute(jsonService.getTrial().getPersons());
    }
}


