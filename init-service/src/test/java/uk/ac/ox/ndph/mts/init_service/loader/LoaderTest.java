package uk.ac.ox.ndph.mts.init_service.loader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.init_service.model.*;
import uk.ac.ox.ndph.mts.init_service.service.PractitionerServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.RoleServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.SiteServiceInvoker;
import uk.ac.ox.ndph.mts.init_service.service.TrialService;

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
    TrialService trialService;

    @Test
    void testLoader() throws Exception {
        Loader loader = new Loader(trialService, practitionerServiceInvoker, roleServiceInvoker, siteServiceInvoker);

        doNothing().when(roleServiceInvoker).execute(anyList());
        doNothing().when(siteServiceInvoker).execute(anyList());
        doNothing().when(practitionerServiceInvoker).execute(anyList());

        when(trialService.getTrial()).thenReturn(mockedTrial());

        loader.run();

        verify(roleServiceInvoker, times(1)).execute(anyList());
        verify(siteServiceInvoker, times(1)).execute(anyList());
        verify(practitionerServiceInvoker, times(1)).execute(anyList());
    }

    Trial mockedTrial() {
        Trial trial = new Trial();
        trial.setTrialName("");

        Site site = new Site();
        site.setName("testSiteName");
        site.setAlias("testSiteAlias");

        Practitioner practitioner= new Practitioner();
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

