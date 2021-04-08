package uk.ac.ox.ndph.mts.siteserviceclient;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.ox.ndph.mts.siteserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.siteserviceclient.common.TestClientBuilder;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteDTO;

import java.net.HttpURLConnection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class GetAllSitesTest {

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
    void TestGetSites_WithValidResponse_ReturnsSitesAsExpected() {

        // Arrange

        var expectedSitesResult = new SiteDTO();
        expectedSitesResult.setSiteId("siteId");
        expectedSitesResult.setParentSiteId("parentSiteId");

        String expectedBodyResponse = "[{\"siteId\":\"siteId\",\"parentSiteId\":\"parentSiteId\"}]";

        webServer.queueResponse(expectedBodyResponse);

        // Act
        List<SiteDTO> actualResponse = siteServiceClient.getAllSites(SiteServiceClient.bearerAuth(token));

        //Assert
        assertAll(
                () -> assertEquals(1 , actualResponse.size()),
                () -> assertEquals(expectedSitesResult.getSiteId() , actualResponse.get(0).getSiteId()),
                () -> assertEquals(expectedSitesResult.getParentSiteId() , actualResponse.get(0).getParentSiteId())
        );
    }

    @Test
    void TestGetSitesById_WhenServiceFails_ThrowsRestException() {
        // Arrange
        webServer.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        // Act
        // Assert
        assertThrows(Exception.class, () -> siteServiceClient.getAllSites(SiteServiceClient.bearerAuth(token)));
    }
}
