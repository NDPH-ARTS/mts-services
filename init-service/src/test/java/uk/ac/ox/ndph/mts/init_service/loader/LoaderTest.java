package uk.ac.ox.ndph.mts.init_service.loader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import uk.ac.ox.ndph.mts.init_service.exception.NullEntityException;
import uk.ac.ox.ndph.mts.init_service.model.Practitioner;
import uk.ac.ox.ndph.mts.init_service.model.Site;
import uk.ac.ox.ndph.mts.init_service.model.Trial;
import uk.ac.ox.ndph.mts.init_service.service.InitProgressReporter;
import uk.ac.ox.ndph.mts.init_service.service.PractitionerServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.RoleServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.SiteServiceInvoker;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoaderTest {

    @Mock
    RoleServiceInvoker roleServiceInvoker;

    @Mock
    SiteServiceInvoker siteServiceInvoker;

    @Mock
    PractitionerServiceInvoker practitionerServiceInvoker;

    @Mock
    DiscoveryClient discoveryClient;

    @Mock
    InitProgressReporter initProgressReporter;

    @Test
    void testLoader() throws Exception {
        Loader loader = new Loader(mockedTrial(),
            practitionerServiceInvoker,
            roleServiceInvoker,
            siteServiceInvoker,
            initProgressReporter,
            discoveryClient);

        doReturn(Collections.singletonList("dummy-role-id")).when(roleServiceInvoker).createManyRoles(anyList(), any(Consumer.class));
        doReturn(Collections.singletonList("dummy-site-id")).when(siteServiceInvoker).execute(anyList());
        doReturn(Arrays.asList("config-server", "site-service", "role-service", "practitioner-service"))
            .when(discoveryClient).getServices();
        doNothing().when(practitionerServiceInvoker).execute(anyList(), anyString());

        loader.run();

        verify(roleServiceInvoker, times(1)).createManyRoles(anyList(), any(Consumer.class));
        verify(siteServiceInvoker, times(1)).execute(anyList());
        verify(practitionerServiceInvoker, times(1)).execute(anyList(), anyString());
    }

    @Test
    void testLoader_ThrowException() throws Exception {
        //doThrow(InterruptedException.class).when(roleServiceInvoker).execute(anyList());
        when(roleServiceInvoker.createManyRoles(anyList(), any(Consumer.class))).thenThrow(new NullEntityException(anyString()));
        doReturn(Arrays.asList("config-server", "site-service", "role-service", "practitioner-service"))
            .when(discoveryClient).getServices();

        Loader loader = new Loader(mockedTrial(), practitionerServiceInvoker, roleServiceClient, siteServiceInvoker, initProgressReporter, discoveryClient);
        Assertions.assertThrows(NullEntityException.class, loader::run);
    }

    /**
     * Register only 1 service (instead of all 4), and expect the loader to be blocked.
     * We verify it is blocked by checking how many times 'getServices' were called in
     * a given time window
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void testLoader_ServicesNotRegistered_LoaderBlocked() throws IOException, InterruptedException {
        doReturn(Arrays.asList("config-server"))
            .when(discoveryClient).getServices();

        Loader loader =
            new Loader(mockedTrial(), practitionerServiceInvoker, roleServiceClient, siteServiceInvoker, initProgressReporter, discoveryClient);

        // start a thread for the runner
        new Thread(() -> {
            try {
                loader.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // verify that the loader called 'getServices' at least 5 times in that time frame, means it stuck
        verify(discoveryClient, timeout(30000).atLeast(5)).getServices();
    }

    @Test
    void testLoader_LoaderWaitForServices_ThenContinue() throws IOException, InterruptedException {
        doReturn(Arrays.asList("config-server", "site-service", "role-service", "practitioner-service"))
            .when(discoveryClient).getServices();

        when(roleServiceInvoker.createMany(anyList(), any(Consumer.class))).thenThrow(new NullEntityException(anyString()));

        Loader loader = new Loader(mockedTrial(), practitionerServiceInvoker, roleServiceInvoker, siteServiceInvoker, initProgressReporter, discoveryClient);
        Assertions.assertThrows(NullEntityException.class, loader::run);

        // Verify the loader wasn't blocked and the number of invocations of getServices is exactly 1
        verify(discoveryClient, timeout(1000).times(1)).getServices();
    }

    Trial mockedTrial() {
        Trial trial = new Trial();
        trial.setTrialName("");

        Site site = new Site();
        site.setName("testSiteName");
        site.setAlias("testSiteAlias");

        Practitioner practitioner = new Practitioner();
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

