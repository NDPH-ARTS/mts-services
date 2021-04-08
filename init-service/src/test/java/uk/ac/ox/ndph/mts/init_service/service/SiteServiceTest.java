package uk.ac.ox.ndph.mts.init_service.service;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.init_service.config.AzureTokenService;
import uk.ac.ox.ndph.mts.siteserviceclient.SiteServiceClient;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteDTO;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteResponseDTO;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SiteServiceTest {

    private static MockWebServer mockBackEnd;
    @Mock
    SiteServiceClient siteServiceClient;

    @Mock
    AzureTokenService azureTokenService;

    @InjectMocks
    SiteServiceInvoker siteServiceInvoker;

    @BeforeAll
    static void setUp() throws IOException {
        // this section uses a custom webclient props
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }


    @BeforeEach
    void setUpEach() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }


    @Test
    void whenDependentServiceFailsWhenNull_CorrectException() {
        assertThrows(Exception.class, () -> siteServiceInvoker.createSites(null));
    }

    @Test
    void whenDependentServiceFails_CorrectException() {
        SiteDTO testSite = new SiteDTO();
        testSite.setName("testName");
        testSite.setAlias("testAlias");

        List<SiteDTO> sites = Collections.singletonList(testSite);
        doThrow(RuntimeException.class).when(siteServiceClient).createEntity(eq(testSite), any(Consumer.class));
        assertThrows(RuntimeException.class, () -> siteServiceInvoker.createSites(sites));
    }

    @Test
    void createSiteListReturnsIds() {
        SiteDTO testSite = new SiteDTO();
        testSite.setSiteId("test-id");
        List<SiteDTO> sites = Collections.singletonList(testSite);
        SiteResponseDTO siteResponseDTO = new SiteResponseDTO("test-id");

        try {
            when(siteServiceClient.createEntity(eq(testSite), any(Consumer.class))).thenReturn(siteResponseDTO);
            List<String> returnedIds = siteServiceInvoker.createSites(sites);
            assertEquals(returnedIds.get(0),siteResponseDTO.getId());
        } catch(Exception e) {
            fail("Should not have thrown any exception");
        }
    }
}
