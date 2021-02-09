package uk.ac.ox.ndph.mts.sample_service.client.site_service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.sample_service.client.TestServiceBackend;
import uk.ac.ox.ndph.mts.sample_service.client.WebClientConfig;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.SiteDTO;
import uk.ac.ox.ndph.mts.sample_service.exception.RestException;
import java.net.HttpURLConnection;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {"spring.cloud.config.enabled=false", "spring.main.allow-bean-definition-overriding=true"})
class SiteServiceClientImplTests {

    public static TestServiceBackend mockBackEnd;

    private SiteServiceClientImpl client;

    private static WebClient.Builder builder;

    @BeforeAll
    static void init() {
        final WebClientConfig config = new WebClientConfig();
        config.setConnectTimeOutMs(500);
        config.setReadTimeOutMs(1000);
        builder = config.webClientBuilder();
    }

    @BeforeEach
    void setUp()  {
        mockBackEnd = TestServiceBackend.autoStart();
        this.client = new SiteServiceClientImpl(builder, mockBackEnd.getUrl());
    }

    @AfterEach
    void cleanup() {
        mockBackEnd.shutdown();
    }

    @Test
    void TestGetAllSites_WithValidResponse_ReturnsSitesAsExpected() {

        // Arrange

        SiteDTO siteDTO = new SiteDTO();
        siteDTO.setSiteId("2");
        siteDTO.setParentSiteId("1");

        SiteDTO[] expectedResponse = {siteDTO};


        String expectedBodyResponse = "[{\"name\":\"root\",\"alias\":\"root\",\"siteId\":\"2\",\"parentSiteId\":\"1\"}]";

        mockBackEnd.queueResponse(expectedBodyResponse);

        // Act
        SiteDTO[] actualResponse = client.getAllSites();

        //Assert
        assertAll(
                () -> assertEquals(expectedResponse.length , actualResponse.length),
                () -> assertEquals(expectedResponse[0].getSiteId(), actualResponse[0].getSiteId()),
                () -> assertEquals(expectedResponse[0].getParentSiteId(), actualResponse[0].getParentSiteId())
        );
    }

    @Test
    void TestGetAllSites_WhenServiceFails_ThrowsRestException() {
        // Arrange
        this.mockBackEnd.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);

        // Act
        // Assert
        assertThrows(RestException.class, () -> client.getAllSites());
    }

}
