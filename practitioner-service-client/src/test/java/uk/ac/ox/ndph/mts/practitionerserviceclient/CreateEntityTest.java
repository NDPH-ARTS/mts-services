package uk.ac.ox.ndph.mts.practitionerserviceclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import uk.ac.ox.ndph.mts.practitionerserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.practitionerserviceclient.common.TestClientBuilder;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.PractitionerDTO;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.ResponseDTO;

import java.io.IOException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CreateEntityTest {

    public static MockWebServerWrapper webServer;
    private static final TestClientBuilder builder = new TestClientBuilder();
    private PractitionerServiceClient practitionerServiceClient;
    private String token = "some-token";
    private Consumer<HttpHeaders> authHeaders = PractitionerServiceClient.bearerAuth(token);

    @SpringBootApplication
    static class TestConfiguration {
    }

    @BeforeAll
    static void beforeAll() {
        webServer = MockWebServerWrapper.newStartedInstance();
    }

    @BeforeEach
    void beforeEach() {
        practitionerServiceClient = builder.build(webServer.getUrl());

    }

    @AfterAll
    static void afterAll() {
        webServer.shutdown();
    }

    @Test
    void testPractitionerService_WhenValidInput() throws IOException {
        PractitionerDTO testPractitioner = new PractitionerDTO();
        testPractitioner.setFamilyName("testFamilyName");
        testPractitioner.setGivenName("testGivenName");
        testPractitioner.setPrefix("Mr");
        ResponseDTO mockResponseFromPractitionerService = new ResponseDTO();
        mockResponseFromPractitionerService.setId("test-id");

        webServer.queueResponse(new ObjectMapper().writeValueAsString(mockResponseFromPractitionerService));
        ResponseDTO returnedPractitionerId = practitionerServiceClient.createEntity(testPractitioner, authHeaders);
        assertNotNull(returnedPractitionerId.getId());
    }

}
