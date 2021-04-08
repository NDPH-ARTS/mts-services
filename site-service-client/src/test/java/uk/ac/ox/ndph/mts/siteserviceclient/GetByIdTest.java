package uk.ac.ox.ndph.mts.siteserviceclient;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.ox.ndph.mts.siteserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.siteserviceclient.common.TestClientBuilder;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteAddressDTO;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteDTO;

import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class GetByIdTest {

    public static MockWebServerWrapper webServer;
    private static final TestClientBuilder builder = new TestClientBuilder();
    private SiteServiceClient siteServiceClient;
    private String token = "some-token";

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
    void withValidResponse_ReturnsSiteWithParentAsExpected() {
        // Arrange
        final var siteId = "siteId";
        final var parentSiteId = "some-parent-site-id";
        SiteAddressDTO siteAddress = new SiteAddressDTO();

        SiteDTO expectedSiteResponse = new SiteDTO();
        expectedSiteResponse.setSiteId(siteId);
        expectedSiteResponse.setParentSiteId(parentSiteId);
        expectedSiteResponse.setAddress(siteAddress);

        String expectedBodyResponse = String.format(
            "{\"createdDateTime\":\"2021-02-07T17:56:23.837542\",\"createdBy\":\"fake-id\"," +
                "\"modifiedDateTime\":\"2021-02-07T17:56:23.837542\",\"modifiedBy\":\"fake-id\"," +
                    "\"name\":\"CCO\"," +
                    "\"alias\":\"CCO\"," +
                    "\"siteId\":\"%s\"," +
                    "\"parentSiteId\":\"%s\"," +
                    "\"address\": %s }", siteId, parentSiteId, siteAddress.toString());

        webServer.queueResponse(expectedBodyResponse);

        // Act
        final SiteDTO actualResponse =
                siteServiceClient.getById(siteId, SiteServiceClient.bearerAuth(token));

        //Assert
        assertAll(
            () -> assertEquals(expectedSiteResponse.getSiteId(), actualResponse.getSiteId()),
            () -> assertEquals(expectedSiteResponse.getParentSiteId(),
                actualResponse.getParentSiteId())
        );
    }

    @Test
    void whenServiceFails_ThrowsException() {
        // Arrange
        webServer.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);

        // Act + Assert
        assertThrows(Exception.class,
            () -> siteServiceClient.getById("any-site-id", SiteServiceClient.bearerAuth(token)));
    }

}
