package uk.ac.ox.ndph.mts.init_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.model.Site;
import uk.ac.ox.ndph.mts.init_service.service.SiteService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SiteServiceTest {

    SiteService siteService;

    MockWebServer mockBackEnd;

    @BeforeEach
    void setUpEach() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        WebClient webClient = WebClient.create(String.format("http://localhost:%s",
                mockBackEnd.getPort()));
        siteService = new SiteService(webClient);
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
        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(testSite))
               .addHeader("Content-Type", "application/json"));
        Site returnedSite = siteService.send(testSite);
        assertNotNull(returnedSite);
    }

    @Test
    void whenDependentServiceFails_CorrectException() {
        Site testSite = new Site();
        testSite.setName("testName");
        testSite.setAlias("testAlias");

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(500));

        List<Site> sites = Collections.singletonList(testSite);
        assertThrows(DependentServiceException.class, () -> siteService.execute(sites));
    }

    @Test
    void createOneSiteList() throws IOException {
        Site testSite = new Site();
        testSite.setName("testName");
        testSite.setAlias("testAlias");
        List<Site> sites = Collections.singletonList(testSite);
        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(testSite))
                .addHeader("Content-Type", "application/json"));
        try {
            siteService.execute(sites);
        } catch(Exception e) {
            fail("Should not have thrown any exception");
        }
    }
}
