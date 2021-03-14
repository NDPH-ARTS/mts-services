package uk.ac.ox.ndph.mts.init_service.loader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.init_service.exception.NullEntityException;
import uk.ac.ox.ndph.mts.init_service.model.Permission;
import uk.ac.ox.ndph.mts.init_service.model.Practitioner;
import uk.ac.ox.ndph.mts.init_service.model.Role;
import uk.ac.ox.ndph.mts.init_service.model.Site;
import uk.ac.ox.ndph.mts.init_service.model.Trial;
import uk.ac.ox.ndph.mts.init_service.service.InitProgressReporter;
import uk.ac.ox.ndph.mts.init_service.service.PractitionerServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.RoleServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.SiteServiceInvoker;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoaderTest {

    @Mock
    RoleServiceInvoker roleServiceInvoker;

    @Mock
    SiteServiceInvoker siteServiceInvoker;

    @Mock
    PractitionerServiceInvoker practitionerServiceInvoker;

    @Mock
    InitProgressReporter initProgressReporter;

    @Test
    void testLoader() throws InterruptedException, IOException {
        Loader loader = new Loader(mockedTrial(), practitionerServiceInvoker, roleServiceInvoker, siteServiceInvoker, initProgressReporter);

        doReturn(Collections.singletonList("dummy-role-id")).when(roleServiceInvoker).execute(anyList());
        doReturn(Collections.singletonList("dummy-site-id")).when(siteServiceInvoker).execute(anyList());
        doNothing().when(practitionerServiceInvoker).execute(anyList(), anyString());

        loader.run();

        verify(roleServiceInvoker, times(1)).execute(anyList());
        verify(siteServiceInvoker, times(1)).execute(anyList());
        verify(practitionerServiceInvoker, times(1)).execute(anyList(), anyString());
    }

    @Test
    void testLoader_ThrowException() throws IOException, InterruptedException {
        //doThrow(InterruptedException.class).when(roleServiceInvoker).execute(anyList());
        when(roleServiceInvoker.execute(anyList())).thenThrow(new NullEntityException(anyString()));

        Loader loader = new Loader(mockedTrial(), practitionerServiceInvoker, roleServiceInvoker, siteServiceInvoker, initProgressReporter);
        Assertions.assertThrows(NullEntityException.class, loader::run);
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

        Permission permission = new Permission();
        permission.setId("testPermission");

        Role role = new Role();
        role.setId("testId");
        role.setPermissions(Collections.singletonList(permission));

        trial.setSites(Collections.singletonList(site));
        trial.setPersons(Collections.singletonList(practitioner));
        trial.setRoles(Collections.singletonList(role));

        return trial;
    }
}

