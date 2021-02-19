package uk.ac.ox.ndph.mts.init_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.model.IDResponse;
import uk.ac.ox.ndph.mts.init_service.model.Practitioner;
import uk.ac.ox.ndph.mts.init_service.model.PractitionerUserAccount;
import uk.ac.ox.ndph.mts.init_service.model.RoleAssignment;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PractitionerServiceTest {

    PractitionerServiceInvoker practitionerServiceInvoker;

    MockWebServer mockBackEnd;

    @BeforeEach
    void setUpEach() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        WebClient webClient = WebClient.create(String.format("http://localhost:%s",
                mockBackEnd.getPort()));
        practitionerServiceInvoker = new PractitionerServiceInvoker(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void testPractitionerService_WhenValidInput() throws IOException {
        Practitioner testPractitioner = new Practitioner();
        testPractitioner.setFamilyName("testFamilyName");
        testPractitioner.setGivenName("testGivenName");
        testPractitioner.setPrefix("Mr");
        IDResponse mockResponseFromPractitionerService = new IDResponse();
        mockResponseFromPractitionerService.setId("test-id");

        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(mockResponseFromPractitionerService))
                .addHeader("Content-Type", "application/json"));
        String returnedPractitionerId = practitionerServiceInvoker.create(testPractitioner);
        assertNotNull(returnedPractitionerId);
    }

    @Test
    void whenDependentServiceFailsWhenNull_CorrectException() {
        assertThrows(Exception.class, () -> practitionerServiceInvoker.execute(null, "dummy-site-id"));
    }

    @Test
    void whenDependentServiceFails_CorrectException() {
        Practitioner testPractitioner = new Practitioner();
        testPractitioner.setFamilyName("testFamilyName");
        testPractitioner.setGivenName("testGivenName");
        testPractitioner.setPrefix("Mr");

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(500));

        List<Practitioner> practitioners = Collections.singletonList(testPractitioner);
        assertThrows(DependentServiceException.class, () -> practitionerServiceInvoker.execute(practitioners, "dummy-site-id"));
    }

    @Test
    void testExecute_WithList_WhenValidInput() throws IOException {
        Practitioner testPractitioner = new Practitioner();
        testPractitioner.setFamilyName("testFamilyName");
        testPractitioner.setGivenName("testGivenName");
        testPractitioner.setPrefix("Mr");
        List<Practitioner> practitioners = Collections.singletonList(testPractitioner);
        IDResponse mockResponseFromPractitionerService = new IDResponse();
        mockResponseFromPractitionerService.setId("test-practitioner-id");
        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(mockResponseFromPractitionerService))
                .addHeader("Content-Type", "application/json"));
        try {
            practitionerServiceInvoker.execute(practitioners, "dummy-site-id");
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testAssignRoleToPractitioner() throws IOException {

        RoleAssignment ra = new RoleAssignment("id-dummy-practitioner", "id-dummy-site", "id-dummy-role");
        IDResponse mockResponseBody = new IDResponse();
        mockResponseBody.setId("id-dummy-role-assignment");
        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(mockResponseBody))
                .addHeader("Content-Type", "application/json"));

        ReflectionTestUtils.setField(practitionerServiceInvoker, "assignRoleEndpoint", "dummy/%s/dummy");
        try {
            practitionerServiceInvoker.assignRoleToPractitioner(ra);
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testLinkUserAccountToPractitioner() throws IOException {
        PractitionerUserAccount userAccount = new PractitionerUserAccount("practitioner-id", "user-account-id");
        IDResponse mockResponseBody = new IDResponse();
        mockResponseBody.setId("id-dummy-link-user-account");
        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(mockResponseBody))
                .addHeader("Content-Type", "application/json"));

        ReflectionTestUtils.setField(practitionerServiceInvoker, "linkUserAccountEndpoint", "dummy/%s/dummy");
        try {
            practitionerServiceInvoker.linkUserAccount(userAccount);
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testExecute_UsesReturnedPractitionerId_inRoleAssignment() {
        Practitioner testPractitioner = new Practitioner();
        testPractitioner.setFamilyName("testFamilyName");
        testPractitioner.setGivenName("testGivenName");
        testPractitioner.setPrefix("Mrs");
        testPractitioner.setRoles(Collections.singletonList("superuser"));
        List<Practitioner> practitioners = Collections.singletonList(testPractitioner);

        String practitionerId = "dummy-practitioner-id";

        PractitionerServiceInvoker mockServiceInvoker = mock(PractitionerServiceInvoker.class, org.mockito.Mockito.CALLS_REAL_METHODS);

        doReturn(practitionerId).when(mockServiceInvoker).create(testPractitioner);
        doNothing().when(mockServiceInvoker).assignRoleToPractitioner(any(RoleAssignment.class));

        mockServiceInvoker.execute(practitioners, "dummy-site-id");

        ArgumentCaptor<RoleAssignment> argumentCaptor = ArgumentCaptor.forClass(RoleAssignment.class);
        verify(mockServiceInvoker).assignRoleToPractitioner(argumentCaptor.capture());
        assertEquals(practitionerId, argumentCaptor.getValue().getPractitionerId());
    }

    @Test
    void testExecute_UsesReturnedPractitionerId_inUserAccount() {
        Practitioner testPractitioner = new Practitioner();
        testPractitioner.setFamilyName("testFamilyName");
        testPractitioner.setGivenName("testGivenName");
        testPractitioner.setPrefix("Mrs");
        testPractitioner.setUserAccount("testUserAccountIdentity");
        List<Practitioner> practitioners = Collections.singletonList(testPractitioner);

        String practitionerId = "dummy-practitioner-id";

        PractitionerServiceInvoker spyServiceInvoker = spy(PractitionerServiceInvoker.class);

        doReturn(practitionerId).when(spyServiceInvoker).create(testPractitioner);
        doNothing().when(spyServiceInvoker).linkUserAccount(any(PractitionerUserAccount.class));

        spyServiceInvoker.execute(practitioners, "dummy-site-id");

        ArgumentCaptor<PractitionerUserAccount> argumentCaptor = ArgumentCaptor.forClass(PractitionerUserAccount.class);
        verify(spyServiceInvoker).linkUserAccount(argumentCaptor.capture());
        assertEquals(practitionerId, argumentCaptor.getValue().getPractitionerId());
    }
}
