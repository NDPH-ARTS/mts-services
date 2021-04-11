package uk.ac.ox.ndph.mts.practitionerserviceclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import uk.ac.ox.ndph.mts.practitionerserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.practitionerserviceclient.common.TestClientBuilder;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.ResponseDTO;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.RoleAssignmentDTO;

import java.util.function.Consumer;

@SpringBootTest
public class AssignRoleToPractitionerTest {

    private static final TestClientBuilder builder = new TestClientBuilder();
    public static MockWebServerWrapper webServer;
    @Mock
    private PractitionerServiceClient practitionerServiceClient;
    private final String token = "some-token";
    private final Consumer<HttpHeaders> authHeaders = PractitionerServiceClient.bearerAuth(token);

    @BeforeAll
    static void beforeAll() {
        webServer = MockWebServerWrapper.newStartedInstance();
    }

    @AfterAll
    static void afterAll() {
        webServer.shutdown();
    }

    @BeforeEach
    void beforeEach() {
        practitionerServiceClient = builder.build(webServer.getUrl());

    }

    @Test
    void testAssignRoleToPractitioner() throws JsonProcessingException {
        RoleAssignmentDTO ra = new RoleAssignmentDTO("id-dummy-practitioner", "id-dummy-site", "id-dummy-role");
        ResponseDTO mockResponseBody = new ResponseDTO();
        mockResponseBody.setId("id-dummy-role-assignment");
        webServer.queueResponse(new ObjectMapper().writeValueAsString(mockResponseBody));
        practitionerServiceClient.assignRoleToPractitioner(ra, authHeaders);

    }

    @SpringBootApplication
    static class TestConfiguration {
    }

}
