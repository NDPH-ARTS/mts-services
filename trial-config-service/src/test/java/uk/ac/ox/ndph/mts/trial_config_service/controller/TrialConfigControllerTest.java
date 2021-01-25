package uk.ac.ox.ndph.mts.trial_config_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.trial_config_service.model.Trial;
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
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    private byte[] getTrialBytes() {

        ObjectMapper mapper = new ObjectMapper();
        byte[] trialBytes = null;
        try {
            trialBytes = mapper.writeValueAsBytes(mockedTrial());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return trialBytes;
    }

    private String getTrialAsJsonData() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonTrial = null;
        try {
            jsonTrial = mapper.writeValueAsString(mockedTrial());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonTrial;
    }



    Trial mockedTrial() {
        Trial trial = new Trial();
        TrialSite trialSite = new TrialSite();
        trialSite.setSiteType(TrialSite.SiteType.CCO);
        trialSite.setSiteName("siteName");
        trialSite.setModifiedBy("me");
        trialSite.setUser(null);


        trialSite.setSiteName("mockYTrialSiteName");
        trialSite.setSiteType(TrialSite.SiteType.CCO);
        trial.setId("trialMockId");
        trial.setTrialName("trialMockId");
        trial.setTrialSites(Collections.singletonList(trialSite));
        trial.setModifiedBy("me");
        trial.setFhirOrganizationId("fhirID");

        return trial;
    }


}
