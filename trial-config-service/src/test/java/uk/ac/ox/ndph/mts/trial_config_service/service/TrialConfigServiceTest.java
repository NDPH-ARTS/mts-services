package uk.ac.ox.ndph.mts.trial_config_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.ac.ox.ndph.mts.trial_config_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.InvalidConfigException;
import uk.ac.ox.ndph.mts.trial_config_service.exception.ResourceAlreadyExistsException;
import uk.ac.ox.ndph.mts.trial_config_service.model.RoleDTO;
import uk.ac.ox.ndph.mts.trial_config_service.model.Trial;
import uk.ac.ox.ndph.mts.trial_config_service.model.TrialRepository;
import uk.ac.ox.ndph.mts.trial_config_service.model.TrialSite;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TrialConfigServiceTest {

    TrialConfigService trialConfigService;

    @Mock
    TrialRepository trialRepository;

    MockWebServer mockBackEnd;

    @BeforeEach
    void setUpEach() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        WebClient webClient = WebClient.create(String.format("http://localhost:%s",
                mockBackEnd.getPort()));
        trialConfigService = new TrialConfigService(trialRepository, webClient);
    }


    @AfterEach
    void tearDown() throws IOException {
        mockBackEnd.shutdown();
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

    @Test
    void testSendToRoleService() throws IOException{

        RoleDTO testRole = new RoleDTO();
        testRole.setRoleName("test role");


        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(testRole))
                .addHeader("Content-Type", "application/json"));

        RoleDTO returnedRole = trialConfigService.sendToRoleService(testRole);

        assertNotNull(returnedRole);
    }

    @Test
    void whenDependentServiceFails_CorrectException() {
        RoleDTO testRole = new RoleDTO();
        testRole.setRoleName("test role");

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(500));

        List<RoleDTO> roles = Collections.singletonList(testRole);
        assertThrows(DependentServiceException.class, () -> trialConfigService.sendToRoleService(roles));
    }


}
