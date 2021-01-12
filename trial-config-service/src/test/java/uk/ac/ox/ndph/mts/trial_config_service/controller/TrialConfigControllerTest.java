package uk.ac.ox.ndph.mts.trial_config_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.ac.ox.ndph.mts.trial_config_service.config.GitRepo;
import uk.ac.ox.ndph.mts.trial_config_service.model.Role;
import uk.ac.ox.ndph.mts.trial_config_service.model.Trial;
import uk.ac.ox.ndph.mts.trial_config_service.model.TrialSite;
import uk.ac.ox.ndph.mts.trial_config_service.model.SiteTypes;
import uk.ac.ox.ndph.mts.trial_config_service.service.TrialConfigService;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrialConfigControllerTest {

    private static final String TEST_CONFIG_ENDPOINT = "anyurlasitsmocked";
    private TrialConfigController trialConfigController;
    public static MockWebServer mockBackEnd;
    ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TrialConfigService trialConfigService;
    @Mock
    private GitRepo gitRepo;
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

    @Test
    void createTrialFromGitRepo() {
        trialConfigController = new TrialConfigController(trialConfigService, null, gitRepo);
        byte[] trialBytes = getTrialBytes();
        when(gitRepo.getTrialFile(any())).thenReturn(trialBytes);
        assertEquals(mockedTrial().getTrialName(), trialConfigController.createTrialFromGitRepo("any").getTrialName());
    }

    @Test
    void createTrialFromJsonData() {
        trialConfigController = new TrialConfigController(trialConfigService, "url");
        String jsonData = getTrialAsJsonData();
        assertEquals(mockedTrial().getTrialName(), trialConfigController.createTrialFromJsonData(jsonData).getTrialName());
    }

    @Test
    void createTrialFromJsonFile() throws Exception {
        String baseUrl = String.format("http://localhost:%s",
            mockBackEnd.getPort());
        trialConfigController = new TrialConfigController(trialConfigService, baseUrl);

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


        Role role = new Role();
        role.setModifiedBy("me");
        role.setRoleName("roleName");

        SiteTypes siteTypes = new SiteTypes();
        siteTypes.setModifiedBy("me");
        siteTypes.setSiteName("siteName");

        trialSite.setSiteName("mockYTrialSiteName");
        trialSite.setSiteType(TrialSite.SiteType.CCO);
        trial.setId("trialMockId");
        trial.setTrialName("trialMockId");
        trial.setTrialSites(Collections.singletonList(trialSite));

        trial.setModifiedBy("me");
        trial.setRoles(Collections.singletonList(role));
        trial.setSiteTypes(Collections.singletonList(siteTypes));
        trial.setFhirOrganizationId("fhirID");

        return trial;
    }


}
