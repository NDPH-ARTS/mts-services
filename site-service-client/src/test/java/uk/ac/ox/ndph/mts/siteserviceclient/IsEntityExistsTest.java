package uk.ac.ox.ndph.mts.siteserviceclient;

import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import uk.ac.ox.ndph.mts.siteserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.siteserviceclient.common.TestClientBuilder;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doThrow;
class IsEntityExistsTest {

    public static MockWebServerWrapper webServer;
    private static final TestClientBuilder builder = new TestClientBuilder();
    private SiteServiceClient siteServiceClient;
    private static String token = "123ert";
    private Consumer<HttpHeaders> authHeaders = SiteServiceClient.bearerAuth(token);


    @SpringBootApplication
    static class TestConfiguration {
    }

    @BeforeAll
    static void beforeAll() {
        webServer = MockWebServerWrapper.newStartedInstance();
    }

    @BeforeEach
    void beforeEach() {
        siteServiceClient = Mockito.spy(builder.build(webServer.getUrl()));
    }

    @AfterAll
    static void afterAll() {
        webServer.shutdown();
    }

    @Test
    void TestEntitySiteExists_WhenExists_ReturnsTrue() {
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.OK.value()));
        boolean idExists = siteServiceClient.entityIdExists("12", authHeaders);
        assertSame(true, idExists);
    }

    @Test
    void TestEntitySiteExists_WhenNotExists_ReturnsFalse() {
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value()));
        boolean idExists = siteServiceClient.entityIdExists("12", authHeaders);
        assertSame(false, idExists);
    }

    @Test
    void TestEntitySiteExists_WhenServiceException_ReturnsRunTimeException() {
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        Assertions.assertThrows(RuntimeException.class, () -> siteServiceClient.entityIdExists("12", authHeaders));
    }

    @Test
    void TestEntitySiteExists_WhenServiceException_ReturnsOtherException() {
        doThrow(new RuntimeException()).when(siteServiceClient).entityIdExists("12", authHeaders);
        Assertions.assertThrows(RuntimeException.class, () -> siteServiceClient.entityIdExists("12", authHeaders));
    }


}
