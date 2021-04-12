package uk.ac.ox.ndph.mts.init_service.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import uk.ac.ox.ndph.mts.init_service.config.AzureTokenService;
import uk.ac.ox.ndph.mts.practitionerserviceclient.PractitionerServiceClient;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.PractitionerDTO;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.PractitionerUserAccountDTO;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.ResponseDTO;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.RoleAssignmentDTO;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Fail.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PractitionerServiceTest {

    private static MockWebServer mockBackEnd;
    @Mock
    PractitionerServiceClient practitionerServiceClient;

    @Mock
    AzureTokenService azureTokenService;

    @InjectMocks
    PractitionerServiceInvoker practitionerServiceInvoker;

    @BeforeAll
    static void setUp() throws IOException {
        // this section uses a custom webclient props
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }


    @BeforeEach
    void setUpEach() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void whenDependentServiceFailsWhenNull_CorrectException() {
        assertThrows(Exception.class, () -> practitionerServiceInvoker.execute(null, "dummy-site-id"));
    }

    @Test
    void whenDependentServiceFails_CorrectException() {
        PractitionerDTO testPractitioner = new PractitionerDTO();
        testPractitioner.setFamilyName("testFamilyName");
        testPractitioner.setGivenName("testGivenName");
        testPractitioner.setPrefix("Mr");

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(500));

        List<PractitionerDTO> practitioners = Collections.singletonList(testPractitioner);
        assertThrows(RuntimeException.class, () -> practitionerServiceInvoker.execute(practitioners, "dummy-site-id"));
    }

    @Test
    void testExecute_WithList_WhenValidInput() {
        PractitionerDTO testPractitioner = new PractitionerDTO();
        testPractitioner.setFamilyName("testFamilyName");
        testPractitioner.setGivenName("testGivenName");
        testPractitioner.setPrefix("Mr");
        List<PractitionerDTO> practitioners = Collections.singletonList(testPractitioner);
        ResponseDTO mockResponseFromPractitionerService = new ResponseDTO();
        mockResponseFromPractitionerService.setId("test-practitioner-id");
        when(practitionerServiceClient.createEntity(eq(testPractitioner), any(Consumer.class))).thenReturn(mockResponseFromPractitionerService);
        try {
            practitionerServiceInvoker.execute(practitioners, "dummy-site-id");
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testExecute_UsesReturnedPractitionerId_inRoleAssignment() {
        PractitionerDTO testPractitioner = new PractitionerDTO();
        testPractitioner.setFamilyName("testFamilyName");
        testPractitioner.setGivenName("testGivenName");
        testPractitioner.setPrefix("Mrs");
        testPractitioner.setRoles(Collections.singletonList("superuser"));
        List<PractitionerDTO> practitioners = Collections.singletonList(testPractitioner);
        ResponseDTO responseDTO = new ResponseDTO("dummy-practitioner-id");

        try {
            when(practitionerServiceClient.createEntity(eq(testPractitioner), any(Consumer.class))).thenReturn(responseDTO);
            practitionerServiceInvoker.execute(practitioners, "dummy-site-id");
            ArgumentCaptor<RoleAssignmentDTO> argumentCaptor = ArgumentCaptor.forClass(RoleAssignmentDTO.class);
            Consumer<HttpHeaders> authHeaders = PractitionerServiceClient.bearerAuth(azureTokenService.getToken());
            verify(practitionerServiceClient).assignRoleToPractitioner(argumentCaptor.capture(), any(Consumer.class));
            assertEquals(responseDTO.getId(), argumentCaptor.getValue().getPractitionerId());

        } catch(Exception e) {
            org.assertj.core.api.Assertions.fail("Should not have thrown any exception");
        }
    }

    @Test
    void testExecute_UsesReturnedPractitionerId_inUserAccount() {
        PractitionerServiceInvoker practServInvkr = new PractitionerServiceInvoker(practitionerServiceClient, azureTokenService);
        PractitionerDTO testPractitioner = new PractitionerDTO();
        testPractitioner.setFamilyName("testFamilyName");
        testPractitioner.setGivenName("testGivenName");
        testPractitioner.setPrefix("Mrs");
        testPractitioner.setUserAccount("testUserAccountIdentity");
        List<PractitionerDTO> practitioners = Collections.singletonList(testPractitioner);

        ResponseDTO responseDTO = new ResponseDTO("dummy-practitioner-id");
        PractitionerServiceInvoker spyServiceInvoker = Mockito.spy(practServInvkr);


        doReturn(responseDTO).when(practitionerServiceClient).createEntity(eq(testPractitioner), any(Consumer.class));
        spyServiceInvoker.execute(practitioners, "dummy-site-id");

        ArgumentCaptor<PractitionerUserAccountDTO> argumentCaptor = ArgumentCaptor.forClass(PractitionerUserAccountDTO.class);
        verify(practitionerServiceClient).linkUserAccount(argumentCaptor.capture(), any(Consumer.class));
        assertEquals(responseDTO.getId(), argumentCaptor.getValue().getPractitionerId());
    }
}
