package uk.ac.ox.ndph.mts.init_service.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Component;
import uk.ac.ox.ndph.mts.init_service.model.Trial;
import uk.ac.ox.ndph.mts.init_service.service.InitProgressReporter;
import uk.ac.ox.ndph.mts.init_service.service.PractitionerServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.SiteServiceInvoker;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@EnableDiscoveryClient
public class Loader implements CommandLineRunner {

    private final PractitionerServiceInvoker practitionerServiceInvoker;
    private final RoleServiceClient roleServiceClient;
    private final SiteServiceInvoker siteServiceInvoker;
    private final Trial trialConfig;
    private final InitProgressReporter initProgressReporter;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${delay-start:1}")
    private long delayStartInSeconds;

    @Autowired
    public Loader(Trial trialConfig,
                  PractitionerServiceInvoker practitionerServiceInvoker,
                  RoleServiceClient roleServiceClient,
                  SiteServiceInvoker siteServiceInvoker,
                  InitProgressReporter initProgressReporter) {
        this.trialConfig = trialConfig;
        this.practitionerServiceInvoker = practitionerServiceInvoker;
        this.roleServiceClient = roleServiceClient;
        this.siteServiceInvoker = siteServiceInvoker;
        this.initProgressReporter = initProgressReporter;
    }


    @Override
    public void run(String... args) throws InterruptedException, IOException {
        // Give the other services some time to come online.
        // We wait and query the discovery to see that our services are up and ready
        initProgressReporter.submitProgress(String.format(LoaderProgress.ENTRY_POINT.message(), delayStartInSeconds));
        Thread.sleep(delayStartInSeconds * 1000);

        var allReady = false;
        while (!allReady) {
            initProgressReporter.submitProgress("Waiting for all services to be registered");

            var applications = this.discoveryClient.getServices();
            initProgressReporter.submitProgress(applications.size() + " services are registered");
            for (var application : applications) {
                initProgressReporter.submitProgress(application + " is registered");
            }

            allReady = applications.containsAll(
                Arrays.asList("config-server", "site-service", "role-service", "practitioner-service"));
            if (allReady) {
                break;
            }

            Thread.sleep(10000);
        }

        initProgressReporter.submitProgress("All services were registered. Continue.");

        try {
            initProgressReporter.submitProgress(LoaderProgress.GET_ROLES_FROM_CONFIG.message());
            var roles = trialConfig.getRoles();

            initProgressReporter.submitProgress(LoaderProgress.CREATE_ROLES.message());
            roleServiceClient.createMany(roles, RoleServiceClient.noAuth());

            initProgressReporter.submitProgress(LoaderProgress.GET_SITES_FROM_CONFIG.message());
            var sites = trialConfig.getSites();

            initProgressReporter.submitProgress(LoaderProgress.CREATE_SITES.message());
            List<String> siteIds = siteServiceInvoker.execute(sites);

            initProgressReporter.submitProgress(LoaderProgress.GET_PERSONS_FROM_CONFIG.message());
            var persons = trialConfig.getPersons();

            initProgressReporter.submitProgress(String.format(
                LoaderProgress.SELECT_FIRST_SITE_FROM_COLLECTION_OF_SIZE.message(), siteIds.size()));
            // Assumption in story https://ndph-arts.atlassian.net/browse/ARTS-164
            String siteIdForUserRoles = siteIds.get(0);
            initProgressReporter.submitProgress(LoaderProgress.CREATE_PRACTITIONER.message());
            practitionerServiceInvoker.execute(persons, siteIdForUserRoles);
            initProgressReporter.submitProgress(LoaderProgress.FINISHED_SUCCESSFULY.message());
        } catch (Exception ex) {
            initProgressReporter.submitProgress(ex.toString());
            initProgressReporter.submitProgress(LoaderProgress.FAILURE.message());
            throw ex;
        }
    }
}


