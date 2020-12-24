package uk.ac.ox.ndph.arts.trialconfigservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.ac.ox.ndph.arts.trialconfigservice.config.WebConfig;
import uk.ac.ox.ndph.arts.trialconfigservice.exception.InvalidConfigException;
import uk.ac.ox.ndph.arts.trialconfigservice.model.Trial;
import uk.ac.ox.ndph.arts.trialconfigservice.model.TrialRepository;
import uk.ac.ox.ndph.arts.trialconfigservice.model.TrialSite;
import uk.ac.ox.ndph.arts.trialconfigservice.service.TrialConfigService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrialConfigControllerTest {

    private static final String TEST_CONFIG_ENDPOINT = "anyurlasitsmocked";

    private TrialConfigController trialConfigController;

    @Mock
    private TrialRepository trialRepository;

    @Mock
    private TrialConfigService trialConfigService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    WebConfig webConfig;

    @BeforeEach
    void setUp() {
        trialConfigController = new TrialConfigController(trialConfigService, trialRepository, webConfig);
    }

    @Test
    void createTrialFromJsonFile() {
        when(webConfig.restTemplate()).thenReturn(restTemplate);
        when(restTemplate.getForEntity(TEST_CONFIG_ENDPOINT, Trial.class)).thenReturn(new ResponseEntity(mockedTrial(), HttpStatus.OK));
        Trial config = trialConfigController.createTrial(TEST_CONFIG_ENDPOINT);
        verify(restTemplate, times(1)).getForEntity(TEST_CONFIG_ENDPOINT, Trial.class);
    }

    @Test
    void invalidConfigErrorWhenURLIncorrect() {
        assertThrows(InvalidConfigException.class, () -> trialConfigController.createTrial(TEST_CONFIG_ENDPOINT));
    }

    Trial mockedTrial(){
        Trial trial = new Trial();
        TrialSite trialSite = new TrialSite();

        trialSite.setSiteName("mockYTrialSiteName");
        trialSite.setSiteType(TrialSite.SiteType.CCO);
        trial.setTrialId("trialMockId");
        trial.setTrialName("trialMockId");
        trial.setTrialSites(Collections.singletonList(trialSite));

        return trial;
    }


}
