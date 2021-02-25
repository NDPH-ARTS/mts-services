package uk.ac.ox.ndph.mts.init_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.init_service.config.AzureTokenService;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.model.IDResponse;
import uk.ac.ox.ndph.mts.init_service.model.Site;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

/*
 * TODO this test is meaningless because it defines a mock site-service API that does not match the site-service.
 *  May as well remove it.
 */
@ExtendWith(MockitoExtension.class)
class SiteServiceTest {

    SiteServiceInvoker siteServiceInvoker;

    MockWebServer mockBackEnd;

    @Mock
    AzureTokenService mockTokenService;

    @BeforeEach
    void setUpEach() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        WebClient webClient = WebClient.create(String.format("http://localhost:%s",
                mockBackEnd.getPort()));
        lenient().when(mockTokenService.getToken()).thenReturn("123ert");
        siteServiceInvoker = new SiteServiceInvoker(webClient, mockTokenService);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void createOneSite() throws IOException {
        Site testSite = new Site();
        testSite.setName("testName");
        testSite.setAlias("testAlias");
        IDResponse mockResponseFromSiteService = new IDResponse();
        mockResponseFromSiteService.setId("test-id");
        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(mockResponseFromSiteService))
               .addHeader("Content-Type", "application/json"));
        String returnedSiteId = siteServiceInvoker.create(testSite);
        assertEquals(returnedSiteId,mockResponseFromSiteService.getId());
    }

    @Test
    void whenDependentServiceFailsWhenNull_CorrectException() {
        assertThrows(Exception.class, () -> siteServiceInvoker.execute(null));
    }

    @Test
    void whenDependentServiceFails_CorrectException() {
        Site testSite = new Site();
        testSite.setName("testName");
        testSite.setAlias("testAlias");

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(500));

        List<Site> sites = Collections.singletonList(testSite);
        assertThrows(DependentServiceException.class, () -> siteServiceInvoker.execute(sites));
    }

    @Test
    void createSiteListReturnsIds() throws IOException {
        Site testSite = new Site();
        testSite.setName("testName");
        testSite.setAlias("testAlias");
        List<Site> sites = Collections.singletonList(testSite);
        IDResponse mockResponseFromSiteService = new IDResponse();
        mockResponseFromSiteService.setId("test-id");

        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(mockResponseFromSiteService))
                .addHeader("Content-Type", "application/json"));
        try {
            List<String> returnedIds = siteServiceInvoker.execute(sites);
            assertEquals(returnedIds.get(0),mockResponseFromSiteService.getId());
        } catch(Exception e) {
            fail("Should not have thrown any exception");
        }
    }
}
