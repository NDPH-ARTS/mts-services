package uk.ac.ox.ndph.mts.siteserviceclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import uk.ac.ox.ndph.mts.siteserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.siteserviceclient.common.TestClientBuilder;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteDTO;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteResponseDTO;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class CreateEntityTest {

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
    void whenCreateSucceeds_responseMatchesId() throws JsonProcessingException {
        final SiteDTO site = new SiteDTO();
        site.setSiteId("the-id");
        final ObjectMapper mapper = new ObjectMapper();
        webServer.queueResponse(mapper.writeValueAsString(new SiteResponseDTO("the-id")));
        final SiteResponseDTO actual = siteServiceClient.createEntity(site, SiteServiceClient.bearerAuth(token));
        //Assert
        assertEquals(actual.getId(), site.getSiteId());
    }

    @Test
    void whenServiceError_thenThrowRestException() {
        // Arrange
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        final SiteDTO site = new SiteDTO();
        site.setSiteId("the-id");
        site.setParentSiteId("parent-id");
        // Act + Assert
        assertThrows(Exception.class, () -> siteServiceClient.createEntity(site, SiteServiceClient.bearerAuth(token)));
    }

    @Test
    void testSiteService_WithNoSiteParent_WhenValidInput() throws IOException {
        SiteDTO testSite = new SiteDTO();
        testSite.setSiteId("testId");
        final ObjectMapper mapper = new ObjectMapper();
        webServer.queueResponse(mapper.writeValueAsString(testSite));
        var returnedSiteId = siteServiceClient.createEntity(testSite, SiteServiceClient.bearerAuth(token));
        assertNotNull(returnedSiteId);
    }

}
