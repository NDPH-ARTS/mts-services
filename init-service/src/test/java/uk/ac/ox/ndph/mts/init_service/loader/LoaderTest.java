package uk.ac.ox.ndph.mts.init_service.loader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.init_service.model.Practitioner;
import uk.ac.ox.ndph.mts.init_service.model.Site;
import uk.ac.ox.ndph.mts.init_service.model.Trial;
import uk.ac.ox.ndph.mts.init_service.service.PractitionerServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.SiteServiceInvoker;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoaderTest {

    @Mock
    RoleServiceClient roleServiceClient;

    @Mock
    SiteServiceInvoker siteServiceInvoker;

    @Mock
    PractitionerServiceInvoker practitionerServiceInvoker;

    @Test
    void testLoader() throws InterruptedException {
        Loader loader = new Loader(mockedTrial(), practitionerServiceInvoker, roleServiceClient, siteServiceInvoker);

        doReturn(Collections.singletonList("dummy-role-id")).when(roleServiceClient).createMany(anyList(), roleServiceClient.noAuth());
        doReturn(Collections.singletonList("dummy-site-id")).when(siteServiceInvoker).execute(anyList());
        doNothing().when(practitionerServiceInvoker).execute(anyList(), anyString());

        loader.run();

        verify(roleServiceClient, times(1)).createMany(anyList(), roleServiceClient.noAuth());
        verify(siteServiceInvoker, times(1)).execute(anyList());
        verify(practitionerServiceInvoker, times(1)).execute(anyList(), anyString());
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

