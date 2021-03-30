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
import uk.ac.ox.ndph.mts.siteserviceclient.exception.RestException;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteDTO;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
        site.setParentSiteId("parent-id");
        final ObjectMapper mapper = new ObjectMapper();
        webServer.queueResponse(mapper.writeValueAsString(site));
        final SiteDTO actual = siteServiceClient.createEntity(site, SiteServiceClient.bearerAuth(token));
        //Assert
        assertThat(actual.getSiteId(), equalTo(site.getSiteId()));
        assertThat(actual.getParentSiteId(), equalTo(site.getParentSiteId()));
    }

    @Test
    void whenServiceError_thenThrowRestException() {
        // Arrange
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        final SiteDTO site = new SiteDTO();
        site.setSiteId("the-id");
        site.setParentSiteId("parent-id");
        // Act + Assert
        assertThrows(RestException.class, () -> siteServiceClient.createEntity(site, SiteServiceClient.bearerAuth(token)));
    }

    @Test
    void testRoleService_WithRoleNoParent_WhenValidInput() throws IOException {
        SiteDTO testSite = new SiteDTO();
        testSite.setSiteId("testId");
        final ObjectMapper mapper = new ObjectMapper();
        webServer.queueResponse(mapper.writeValueAsString(testSite));
        var returnedRoleId = siteServiceClient.createEntity(testSite, SiteServiceClient.bearerAuth(token));
        assertNotNull(returnedRoleId);
    }

}
