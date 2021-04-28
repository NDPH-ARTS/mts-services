package uk.ac.ox.ndph.mts.init_service.loader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import uk.ac.ox.ndph.mts.init_service.exception.NullEntityException;
import uk.ac.ox.ndph.mts.init_service.model.Trial;
import uk.ac.ox.ndph.mts.init_service.repository.PractitionerStore;
import uk.ac.ox.ndph.mts.init_service.repository.RoleRepository;
import uk.ac.ox.ndph.mts.init_service.repository.SiteStore;
import uk.ac.ox.ndph.mts.init_service.service.InitProgressReporter;
import uk.ac.ox.ndph.mts.init_service.model.PractitionerDTO;
import uk.ac.ox.ndph.mts.init_service.model.PermissionDTO;
import uk.ac.ox.ndph.mts.init_service.model.RoleDTO;
import uk.ac.ox.ndph.mts.init_service.model.SiteDTO;

import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoaderTest {

    @Mock
    PractitionerStore practitionerStore;
    
    @Mock
    RoleRepository roleRepository;
    
    @Mock
    SiteStore siteStore;
    
    @Mock
    DiscoveryClient discoveryClient;

    @Mock
    InitProgressReporter initProgressReporter;

    @Test
    void testLoader() throws Exception {
        Loader loader = new Loader(mockedTrial(), practitionerStore, roleRepository, siteStore,
                            initProgressReporter, discoveryClient);
        
        when(roleRepository.saveAll(anyList())).thenReturn(Collections.singletonList(new RoleDTO()));
        when(siteStore.saveEntities(anyList())).thenReturn(Collections.singletonList("dummy-site-id"));
        when(discoveryClient.getServices()).thenReturn(asList("config-server"));
        

        loader.run();

        verify(roleRepository, times(1)).saveAll(anyList());
        verify(siteStore, times(1)).saveEntities(anyList());
        verify(practitionerStore, times(1)).save(any(PractitionerDTO.class));
    }

    @Test
    void testLoader_ThrowException() {
        when(discoveryClient.getServices()).thenReturn(asList("config-server"));
        when(roleRepository.saveAll(anyList())).thenThrow(new NullEntityException(anyString()));

        Loader loader = new Loader(mockedTrial(), practitionerStore, roleRepository, siteStore,
                            initProgressReporter, discoveryClient);
        Assertions.assertThrows(NullEntityException.class, loader::run);
    }

    /**
     * Register only 1 service (instead of all 4), and expect the loader to be blocked.
     * We verify it is blocked by checking how many times 'getServices' were called in
     * a given time window
     */
    @Test
    void testLoader_ServicesNotRegistered_LoaderBlocked() {
        doReturn(emptyList()).when(discoveryClient).getServices();

        Loader loader = new Loader(mockedTrial(), practitionerStore, roleRepository, siteStore,
            initProgressReporter, discoveryClient);

        // start a thread for the runner
        new Thread(() -> {
            try {
                loader.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // verify that the loader called 'getServices' at least 5 times in that time frame, means it stuck
        verify(discoveryClient, timeout(30000).atLeast(5)).getServices();
    }

    @Test
    void testLoader_LoaderWaitForServices_ThenContinue() {
        doReturn(asList("config-server", "site-service", "role-service", "practitioner-service"))
            .when(discoveryClient).getServices();

        when(roleRepository.saveAll(anyList())).thenThrow(new NullEntityException(anyString()));

        Loader loader = new Loader(mockedTrial(), practitionerStore, roleRepository, siteStore,
                            initProgressReporter, discoveryClient);

        Assertions.assertThrows(NullEntityException.class, loader::run);

        // Verify the loader wasn't blocked and the number of invocations of getServices is exactly 1
        verify(discoveryClient, timeout(1000).times(1)).getServices();
    }

    Trial mockedTrial() {
        Trial trial = new Trial();
        trial.setTrialName("");

        SiteDTO site = new SiteDTO();
        site.setName("testSiteName");
        site.setAlias("testSiteAlias");

        PractitionerDTO practitioner = new PractitionerDTO();
        practitioner.setFamilyName("testFamilyName");
        practitioner.setGivenName("testGivenName");
        practitioner.setPrefix("Mr");

        PermissionDTO permission = new PermissionDTO();
        permission.setId("testPermission");

        RoleDTO role = new RoleDTO();
        role.setId("testId");
        role.setPermissions(Collections.singletonList(permission));

        trial.setSites(Collections.singletonList(site));
        trial.setPersons(Collections.singletonList(practitioner));
        trial.setRoles(Collections.singletonList(role));

        return trial;
    }
}

