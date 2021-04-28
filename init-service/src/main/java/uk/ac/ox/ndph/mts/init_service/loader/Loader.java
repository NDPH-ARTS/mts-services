package uk.ac.ox.ndph.mts.init_service.loader;

import java.util.Arrays;
import java.util.List;

import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import uk.ac.ox.ndph.mts.init_service.exception.NullEntityException;
import uk.ac.ox.ndph.mts.init_service.model.Practitioner;
import uk.ac.ox.ndph.mts.init_service.model.Trial;
import uk.ac.ox.ndph.mts.init_service.repository.PractitionerStore;
import uk.ac.ox.ndph.mts.init_service.repository.RoleRepository;
import uk.ac.ox.ndph.mts.init_service.repository.SiteStore;
import uk.ac.ox.ndph.mts.init_service.service.InitProgressReporter;

@Component
public class Loader implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(Loader.class);

    private final PractitionerStore practitionerStore;
    private final RoleRepository roleRepository;
    private final SiteStore siteStore;
    private final Trial trialConfig;
    private final InitProgressReporter initProgressReporter;
    private final DiscoveryClient discoveryClient;

    @Value("${delay-start:1}")
    private long delayStartInSeconds;

    @Autowired
    public Loader(Trial trialConfig,
                  PractitionerStore practitionerStore,
                  RoleRepository roleServiceInvoker,
                  SiteStore siteStore,
                  InitProgressReporter initProgressReporter,
                  DiscoveryClient discoveryClient) {
        this.trialConfig = trialConfig;
        this.practitionerStore = practitionerStore;
        this.roleRepository = roleServiceInvoker;
        this.siteStore = siteStore;
        this.initProgressReporter = initProgressReporter;
        this.discoveryClient = discoveryClient;
    }


    @Override
    public void run(String... args) throws Exception {
        // Give the other services some time to come online.
        // We wait and query the discovery to see that our services are up and ready
        initProgressReporter.submitProgress(String.format(LoaderProgress.ENTRY_POINT.message(), delayStartInSeconds));
        Thread.sleep(delayStartInSeconds * 1000);

        boolean allReady;
        do {
            initProgressReporter.submitProgress(LoaderProgress.WAITING_FOR_ALL.message());

            var applications = this.discoveryClient.getServices();
            initProgressReporter.submitProgress(
                String.format(LoaderProgress.N_SERVICES_REGISTERED.message(), applications.size()));
            for (var application : applications) {
                initProgressReporter.submitProgress(
                    String.format(LoaderProgress.SERVICE_REGISTERED.message(), application));
            }

            allReady = applications.containsAll(Arrays.asList("config-server"));
            if (!allReady) {
                Thread.sleep(5000);
            }
        } while (!allReady);

        initProgressReporter.submitProgress(LoaderProgress.ALL_REGISTERED.message());

        try {
            initProgressReporter.submitProgress(LoaderProgress.GET_ROLES_FROM_CONFIG.message());
            var roles = trialConfig.getRoles();

            initProgressReporter.submitProgress(LoaderProgress.CREATE_ROLES.message());
            roleRepository.saveAll(roles);

            initProgressReporter.submitProgress(LoaderProgress.GET_SITES_FROM_CONFIG.message());
            var sites = trialConfig.getSites();

            initProgressReporter.submitProgress(LoaderProgress.CREATE_SITES.message());
            List<String> siteIds = siteStore.saveEntities(sites);

            initProgressReporter.submitProgress(LoaderProgress.GET_PERSONS_FROM_CONFIG.message());
            var persons = trialConfig.getPersons();

            initProgressReporter.submitProgress(String.format(
                LoaderProgress.SELECT_FIRST_SITE_FROM_COLLECTION_OF_SIZE.message(), siteIds.size()));
            // Assumption in story https://ndph-arts.atlassian.net/browse/ARTS-164
            String siteIdForUserRoles = siteIds.get(0);
            initProgressReporter.submitProgress(LoaderProgress.CREATE_PRACTITIONER.message());
            
            createPractitioners(persons, siteIdForUserRoles);
            
            
            initProgressReporter.submitProgress(LoaderProgress.FINISHED_SUCCESSFULY.message());
        } catch (Exception ex) {
            initProgressReporter.submitProgress(ex.toString());
            initProgressReporter.submitProgress(LoaderProgress.FAILURE.message());
            throw ex;
        }
    }


    private void createPractitioners(List<Practitioner> practitioners, String siteId) {
        if (practitioners == null) {
            throw new NullEntityException("No Practitioners in payload");
        }

        for (Practitioner practitioner : practitioners) {
            LOGGER.info("Starting to create practitioner(s): {}", practitioner);

            String practitionerId = practitionerStore.save(practitioner);

            if (practitioner.getRoles() != null) {
                LOGGER.info("Assigning roles to practitioner(s): {} {}", practitionerId, practitioner.getRoles());
                for (String roleId : practitioner.getRoles()) {
                    PractitionerRole role = new PractitionerRole();
                    role.setOrganization(new Reference("Organization/" + siteId));
                    role.setPractitioner(new Reference("Practitioner/" + practitionerId));
                    role.addCode().setText(roleId);
                    practitionerStore.savePractitionerRole(role);
                }
            }
        }
        LOGGER.info("Finished creating {} practitioner", practitioners.size());
    }
}


