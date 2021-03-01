package uk.ac.ox.ndph.mts.roleserviceclient;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import uk.ac.ox.ndph.mts.roleserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.roleserviceclient.common.TestClientBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AuthTest {

    public static MockWebServerWrapper webServer;
    private static final TestClientBuilder builder = new TestClientBuilder();
    private RoleServiceClient roleServiceClient;

    @SpringBootApplication
    static class TestConfiguration {
    }

    @BeforeAll
    static void beforeAll() {
        webServer = MockWebServerWrapper.newStartedInstance();
    }

    @BeforeEach
    void beforeEach() {
        roleServiceClient = builder.build(webServer.getUrl());
    }

    @AfterAll
    static void afterAll() {
        webServer.shutdown();
    }

    @Test
    void whenNoAuth_thenNoHeadersReceived() {
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.OK.value()));
        assertTrue(roleServiceClient.idExists("some-id", RoleServiceClient.noAuth()));
        final RecordedRequest request = webServer.takeRequest();
        assertNull(request.getHeaders().get("Authorization"));
    }

    @Test
    void whenaBasicAuth_thenBasicAuthHeaderReceived() {
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.OK.value()));
        assertTrue(roleServiceClient.idExists("some-id", RoleServiceClient.basicAuth("user", "pass")));
        final RecordedRequest request = webServer.takeRequest();
        assertThat(request.getHeaders().get("Authorization"), startsWith("Basic"));
    }

    @Test
    void whenaBearerAuth_thenTokenHeaderReceived() {
        final String token = "this-is-the-random-token";
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.OK.value()));
        assertTrue(roleServiceClient.idExists("some-id", RoleServiceClient.bearerAuth(token)));
        final RecordedRequest request = webServer.takeRequest();
        final String authHeader = request.getHeaders().get("Authorization");
        assertThat(authHeader, startsWith("Bearer"));
        assertThat(authHeader, containsString(token));
    }

}
