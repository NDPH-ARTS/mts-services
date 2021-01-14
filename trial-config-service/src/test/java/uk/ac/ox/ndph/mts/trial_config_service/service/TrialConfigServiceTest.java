package uk.ac.ox.ndph.mts.trial_config_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.trial_config_service.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.ResourceAlreadyExistsException;
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

    @Mock
    WebClient webClient;

    @BeforeEach
    void setUp() {
        trialConfigService = new TrialConfigService(trialRepository, webClient);
    }

    private static final String DUMMY_OID = "dummy-oid";

    @Test
    void createInitialTrial() {
        Trial testTrial = new Trial();
        testTrial.setTrialName("testTrial");
        testTrial.setId("testId");
        TrialSite testTrialSite = new TrialSite(TrialSite.SiteType.CCO);
        testTrialSite.setSiteName("testTrialSiteName");
        testTrial.setTrialSites(Collections.singletonList(testTrialSite));

        when(trialRepository.save(Mockito.any(Trial.class))).thenAnswer(i -> i.getArguments()[0]);
        Trial savedTrial = trialConfigService.saveTrial(testTrial, DUMMY_OID);

        assertEquals(Trial.Status.IN_CONFIGURATION, savedTrial.getStatus());

        assertEquals(testTrial.getTrialName(), savedTrial.getTrialName());
        assertEquals(testTrial.getId(), savedTrial.getId());

        assertEquals(DUMMY_OID, savedTrial.getTrialSites().get(0).getUser().getAzureOid());
        assertEquals(testTrialSite.getSiteName(), savedTrial.getTrialSites().get(0).getSiteName());

        assertEquals(DUMMY_OID, savedTrial.getTrialSites().get(0).getUser().getAzureOid());

        assertEquals(DUMMY_OID, savedTrial.getModifiedBy());
        assertEquals(DUMMY_OID, savedTrial.getTrialSites().get(0).getModifiedBy());
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
        assertThrows(InvalidConfigException.class, () -> trialConfigService.saveTrial(invalidConfig, DUMMY_OID));
    }
}
