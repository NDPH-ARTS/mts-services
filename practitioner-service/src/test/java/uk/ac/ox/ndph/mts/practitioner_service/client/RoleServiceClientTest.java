package uk.ac.ox.ndph.mts.practitioner_service.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.practitioner_service.configuration.WebClientConfig;
import uk.ac.ox.ndph.mts.practitioner_service.exception.RestException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest(properties = {"spring.cloud.config.discovery.enabled = false", "spring.cloud.config.enabled=false", "spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles("test-all-required")
public class RoleServiceClientTest {

    public static MockWebServer mockBackEnd;

    private AbstractEntityServiceClient roleServiceClient;

    private static WebClient.Builder builder;

    @BeforeAll
    static void setUp() throws IOException {
        // this section uses a custom webclient props
        final WebClientConfig config = new WebClientConfig();
        config.setConnectTimeOutMs(500);
        config.setReadTimeOutMs(1000);
        builder = config.webClientBuilder();

        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        roleServiceClient = new RoleServiceClient(builder, baseUrl);
    }

    @Test
    void TestEntityRoleExists_WhenExists_ReturnsTrue() {
        mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.SC_OK));
        boolean idExists = roleServiceClient.entityIdExists("12");
        assertSame(idExists, true);
    }

    @Test
    void TestEntityRoleExists_WhenNotExists_ReturnsFalse() {
        mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.SC_NOT_FOUND));
        boolean idExists = roleServiceClient.entityIdExists("12");
        assertSame(idExists, false);
    }

    @Test
    void TestEntityRoleExists_WhenServiceException_ReturnsRestException() {
        mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR));
        Assertions.assertThrows(RestException.class, () -> roleServiceClient.entityIdExists("12"));
    }

}
