package uk.ac.ox.ndph.mts.init_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.model.Practitioner;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(testPractitioner))
               .addHeader("Content-Type", "application/json"));
        Practitioner returnedPractitioner = practitionerServiceInvoker.send(testPractitioner);
        assertNotNull(returnedPractitioner);
    }

    @Test
    void whenDependentServiceFailsWhenNull_CorrectException() {
        assertThrows(Exception.class, () -> practitionerServiceInvoker.execute(null));
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
        assertThrows(DependentServiceException.class, () -> practitionerServiceInvoker.execute(practitioners));
    }

    @Test
    void testPractitionerService_WithList_WhenValidInput() throws IOException {
        Practitioner testPractitioner = new Practitioner();
        testPractitioner.setFamilyName("testFamilyName");
        testPractitioner.setGivenName("testGivenName");
        testPractitioner.setPrefix("Mr");
        List<Practitioner> practitioners = Collections.singletonList(testPractitioner);
        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(testPractitioner))
                .addHeader("Content-Type", "application/json"));
        try {
            practitionerServiceInvoker.execute(practitioners);
        } catch(Exception e) {
            fail("Should not have thrown any exception");
        }
    }
}
