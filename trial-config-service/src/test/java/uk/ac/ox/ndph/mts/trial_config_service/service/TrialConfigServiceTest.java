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
import uk.ac.ox.ndph.mts.trial_config_service.model.SiteTypes;

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


    @Test
    void createInitialTrial() {
        Trial testTrial = new Trial();
        testTrial.setTrialName("testTrial");
        testTrial.setId("testId");
        TrialSite testTrialSite = new TrialSite(TrialSite.SiteType.CCO);
        testTrialSite.setSiteName("testTrialSiteName");

        SiteTypes testSiteTypes = new SiteTypes();
        testSiteTypes.setSiteName("testTrialSiteName");
        testSiteTypes.setSiteDescription("testTrialSiteName");

        Role testRole = new Role();
        testRole.setRoleName("testRoleName");

        testTrial.setTrialSites(Collections.singletonList(testTrialSite));
        testTrial.setRoles(Collections.singletonList(testRole));
        testTrial.setSiteTypes(Collections.singletonList(testSiteTypes));

        when(trialRepository.save(Mockito.any(Trial.class))).thenAnswer(i -> i.getArguments()[0]);
        Trial savedTrial = trialConfigService.saveTrial(testTrial, DUMMY_OID);

        assertEquals(Trial.Status.IN_CONFIGURATION, savedTrial.getStatus());

        assertEquals(testTrial.getTrialName(), savedTrial.getTrialName());
        assertEquals(testTrial.getId(), savedTrial.getId());

        assertEquals(DUMMY_OID, savedTrial.getTrialSites().get(0).getUser().getAzureOid());
        assertEquals(testTrialSite.getSiteName(), savedTrial.getTrialSites().get(0).getSiteName());

        assertEquals(DUMMY_OID, savedTrial.getTrialSites().get(0).getUser().getAzureOid());

        assertEquals(testTrial.getRoles().size(), savedTrial.getRoles().size());
        assertEquals(testTrial.getRoles().get(0).getRoleName(), savedTrial.getRoles().get(0).getRoleName());

        assertEquals(testTrial.getSiteTypes().size(), testTrial.getSiteTypes().size());
        assertEquals(testTrial.getSiteTypes().get(0).getSiteName(), testTrial.getSiteTypes().get(0).getSiteName());
        assertEquals(testTrial.getSiteTypes().get(0).getSiteDescription(), testTrial.getSiteTypes().get(0).getSiteDescription());

        assertEquals(DUMMY_OID, savedTrial.getModifiedBy());
        assertEquals(DUMMY_OID, savedTrial.getTrialSites().get(0).getModifiedBy());
        assertEquals(DUMMY_OID, savedTrial.getRoles().get(0).getModifiedBy());
    }

    @Test
    void resourceAlreadyExistsErrorThrown() {
        Trial t = new Trial();
        when(trialRepository.existsById(any())).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> trialConfigService.saveTrial(t, DUMMY_OID));
    }

    @Test
    void invalidConfigExceptionThrownForNoRoot() {
        Trial invalidConfig = new Trial();
        invalidConfig.setTrialSites(Collections.singletonList(new TrialSite()));
        invalidConfig.setSiteTypes(Collections.singletonList(new SiteTypes()));
        assertThrows(InvalidConfigException.class, () -> trialConfigService.saveTrial(invalidConfig, DUMMY_OID));
    }
}
