package uk.ac.ox.ndph.mts.trial_config_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.ac.ox.ndph.mts.trial_config_service.config.WebConfig;
import uk.ac.ox.ndph.mts.trial_config_service.model.Trial;
import uk.ac.ox.ndph.mts.trial_config_service.model.TrialRepository;
import uk.ac.ox.ndph.mts.trial_config_service.model.TrialSite;
import uk.ac.ox.ndph.mts.trial_config_service.service.TrialConfigService;

import java.io.IOException;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class TrialConfigControllerTest {

    private static final String TEST_CONFIG_ENDPOINT = "anyurlasitsmocked";

    private TrialConfigController trialConfigController;

    public static MockWebServer mockBackEnd;

    ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TrialConfigService trialConfigService;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());
        trialConfigController = new TrialConfigController(trialConfigService, baseUrl);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void createTrialFromJsonFile() throws Exception {

        Trial mockTrial = mockedTrial();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(mockTrial))
                .addHeader("Content-Type", "application/json"));
        Mono<Trial> mockResponseTrial = trialConfigController.createTrialFromURL(TEST_CONFIG_ENDPOINT);

        StepVerifier.create(mockResponseTrial)
                .expectNextMatches(trial -> trial.getId().equals(mockTrial.getId()) &&
                        trial.getTrialName().equals(mockTrial.getTrialName()) &&
                        trial.getTrialSites().get(0).getSiteType().equals(mockTrial.getTrialSites().get(0).getSiteType()))
                .verifyComplete();

    }


    Trial mockedTrial() {
        Trial trial = new Trial();
        TrialSite trialSite = new TrialSite();

        trialSite.setSiteName("mockYTrialSiteName");
        trialSite.setSiteType(TrialSite.SiteType.CCO);
        trial.setId("trialMockId");
        trial.setTrialName("trialMockId");
        trial.setTrialSites(Collections.singletonList(trialSite));

        return trial;
    }


}
