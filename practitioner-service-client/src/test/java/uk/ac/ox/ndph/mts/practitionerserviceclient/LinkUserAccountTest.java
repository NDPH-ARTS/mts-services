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
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.PractitionerUserAccountDTO;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.ResponseDTO;

import java.util.function.Consumer;

import static org.assertj.core.api.Fail.fail;

@SpringBootTest
public class LinkUserAccountTest {

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
    void testLinkUserAccountToPractitioner() throws JsonProcessingException {
        PractitionerUserAccountDTO userAccount = new PractitionerUserAccountDTO("practitioner-id", "user-account-id");
        ResponseDTO mockResponseBody = new ResponseDTO();
        mockResponseBody.setId("id-dummy-link-user-account");
        webServer.queueResponse(new ObjectMapper().writeValueAsString(mockResponseBody));

        try {
            practitionerServiceClient.linkUserAccount(userAccount, authHeaders);
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
    }

    @SpringBootApplication
    static class TestConfiguration {
    }

}

