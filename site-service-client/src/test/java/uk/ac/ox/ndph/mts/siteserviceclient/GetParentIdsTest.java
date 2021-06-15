package uk.ac.ox.ndph.mts.siteserviceclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.ox.ndph.mts.siteserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.siteserviceclient.common.TestClientBuilder;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class GetParentIdsTest {

    public static MockWebServerWrapper webServer;

    private static final TestClientBuilder builder = new TestClientBuilder();

    private SiteServiceClient siteServiceClient;

    private static String token = "123ert";

    @SpringBootApplication
    static class TestConfiguration {
    }

    @BeforeAll
    static void beforeAll() {
        webServer = MockWebServerWrapper.newStartedInstance();
    }

    @BeforeEach
    void beforeEach() {
        siteServiceClient = builder.build(webServer.getUrl());

    }

    @AfterAll
    static void afterAll() {
        webServer.shutdown();
    }

    @Test
    void TestGetSites_WithValidResponse_ReturnsSitesAsExpected() throws JsonProcessingException {

        // Arrange

        List<String> result = Arrays.asList("siteId1", "siteId2");

        final ObjectMapper mapper = new ObjectMapper();
        webServer.queueResponse(mapper.writeValueAsString(result));

        var actual = siteServiceClient.getParentSiteIds("childsiteId",
            SiteServiceClient.bearerAuth(token));

        assertThat(actual.get(0), equalTo(result.get(0)));
        assertThat(actual.get(1), equalTo(result.get(1)));
        assertThat(actual.size(), equalTo(result.size()));

    }

    @Test
    void TestGetSitesById_WhenServiceFails_ThrowsRestException() {
        // Arrange
        webServer.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        // Act
        // Assert
        assertThrows(Exception.class, () -> siteServiceClient.getParentSiteIds("childsiteId",
            SiteServiceClient.bearerAuth(token)));
    }
}
