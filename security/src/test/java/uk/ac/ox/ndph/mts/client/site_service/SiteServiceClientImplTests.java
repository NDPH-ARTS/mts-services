package uk.ac.ox.ndph.mts.client.site_service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.client.TestServiceBackend;
import uk.ac.ox.ndph.mts.client.dtos.SiteDTO;
import uk.ac.ox.ndph.mts.security.authentication.SecurityContextUtil;
import uk.ac.ox.ndph.mts.security.exception.RestException;
import java.net.HttpURLConnection;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes=SecurityContextUtil.class)
class SiteServiceClientImplTests {

    public static TestServiceBackend mockBackEnd;

    private SiteServiceClientImpl client;

    private static WebClient.Builder builder;

    @MockBean
    private SecurityContextUtil securityContextUtil;

    @BeforeAll
    static void init() { builder = WebClient.builder(); }

    @BeforeEach
    void setUp()  {
        mockBackEnd = TestServiceBackend.autoStart();
        this.client = new SiteServiceClientImpl(builder, mockBackEnd.getUrl(),securityContextUtil);
        Mockito.when(securityContextUtil.getToken()).thenReturn("mocktoken");
    }

    @AfterEach
    void cleanup() {
        mockBackEnd.shutdown();
    }

    @Test
    void TestGetSites_WithValidResponse_ReturnsSitesAsExpected() {

        // Arrange

        var expectedSitesResult = new SiteDTO();
        expectedSitesResult.setSiteId("siteId");
        expectedSitesResult.setParentSiteId("parentSiteId");

        String expectedBodyResponse = "[{\"siteId\":\"siteId\",\"parentSiteId\":\"parentSiteId\"}]";

        mockBackEnd.queueResponse(expectedBodyResponse);

        // Act
        List<SiteDTO> actualResponse = client.getAllSites();

        //Assert
        assertAll(
                () -> assertEquals(1 , actualResponse.size()),
                () -> assertEquals(expectedSitesResult.getSiteId() , actualResponse.get(0).getSiteId()),
                () -> assertEquals(expectedSitesResult.getParentSiteId() , actualResponse.get(0).getParentSiteId())
        );
    }

    @Test
    void TestGetRolesById_WhenServiceFails_ThrowsRestException() {
        // Arrange
        mockBackEnd.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        // Act
        // Assert
        assertThrows(RestException.class, () -> client.getAllSites());
    }
}
