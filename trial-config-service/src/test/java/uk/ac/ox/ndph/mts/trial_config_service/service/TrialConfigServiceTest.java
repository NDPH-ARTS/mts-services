package uk.ac.ox.ndph.mts.trial_config_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.trial_config_service.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.ResourceAlreadyExistsException;
import uk.ac.ox.ndph.mts.trial_config_service.model.Role;
import uk.ac.ox.ndph.mts.trial_config_service.model.Trial;
import uk.ac.ox.ndph.mts.trial_config_service.model.TrialRepository;
import uk.ac.ox.ndph.mts.trial_config_service.model.TrialSite;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TrialConfigServiceTest {

    private TrialConfigService trialConfigService;

    @Mock
    TrialRepository trialRepository;

    @BeforeEach
    void setUp() {
        trialConfigService = new TrialConfigService(trialRepository);
    }

    private static final String DUMMY_OID = "dummy-oid";

    /*
    @Test
    void createInitialTrial() {
        Trial testTrial = new Trial();
        testTrial.setTrialName("testTrial");
        testTrial.setId("testId");
        TrialSite testTrialSite = new TrialSite(TrialSite.SiteType.CCO);
        testTrialSite.setSiteName("testTrialSiteName");

        Role testRole = new Role();
        testRole.setRoleName("testRoleName");

        testTrial.setTrialSites(Collections.singletonList(testTrialSite));
        testTrial.setRoles(Collections.singletonList(testRole));

        when(trialRepository.save(Mockito.any(Trial.class))).thenAnswer(i -> i.getArguments()[0]);
        Trial savedTrial = trialConfigService.saveTrial(testTrial, DUMMY_OID);

        assertEquals(savedTrial.getStatus(), Trial.Status.IN_CONFIGURATION);

        assertEquals(savedTrial.getTrialName(), testTrial.getTrialName());
        assertEquals(savedTrial.getId(), testTrial.getId());

        assertEquals(savedTrial.getTrialSites().get(0).getUser().getAzureOid(), DUMMY_OID);
        assertEquals(savedTrial.getTrialSites().get(0).getSiteName(), testTrialSite.getSiteName());

        assertEquals(savedTrial.getTrialSites().get(0).getUser().getAzureOid(), DUMMY_OID);

        assertEquals(savedTrial.getRoles().size(), testTrial.getRoles().size());
        assertEquals(savedTrial.getRoles().get(0).getRoleName(), testTrial.getRoles().get(0).getRoleName());

        assertEquals(savedTrial.getModifiedBy(), DUMMY_OID);
    }

    @Test
    void resourceAlreadyExistsErrorThrown(){
        when(trialRepository.existsById(any())).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> trialConfigService.saveTrial(new Trial(), DUMMY_OID));
    }

    @Test
    void invalidConfigExceptionThrownForNoRoot(){
        Trial invalidConfig = new Trial();
        invalidConfig.setTrialSites(Collections.singletonList(new TrialSite()));
        assertThrows(InvalidConfigException.class, () -> trialConfigService.saveTrial(invalidConfig, DUMMY_OID));
    }

     */
}
