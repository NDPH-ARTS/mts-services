package uk.ac.ox.ndph.mts.init_service.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.init_service.service.JsonService;
import uk.ac.ox.ndph.mts.init_service.service.PractitionerService;
import uk.ac.ox.ndph.mts.init_service.service.RoleService;
import uk.ac.ox.ndph.mts.init_service.service.SiteService;

@Component
public class Loader implements CommandLineRunner {

    @Autowired
    private JsonService jsonService;

    @Autowired
    private PractitionerService practitonerService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SiteService siteService;

    @Override
    public void run(String... args) {
        roleService.execute(jsonService.getTrial().getRoles());
        siteService.execute(jsonService.getTrial().getSites());
        practitonerService.execute(jsonService.getTrial().getPersons());

    }

}


