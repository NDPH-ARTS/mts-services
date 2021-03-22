//package uk.ac.ox.ndph.mts.init_service.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.reactive.function.client.WebClient;
//import uk.ac.ox.ndph.mts.init_service.config.AzureTokenService;
//import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
//import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
//import uk.ac.ox.ndph.mts.roleserviceclient.configuration.ClientRoutesConfig;
//import uk.ac.ox.ndph.mts.roleserviceclient.configuration.WebClientConfig;
//import uk.ac.ox.ndph.mts.roleserviceclient.model.PermissionDTO;
//import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.List;
//
//
//import static org.assertj.core.api.Assertions.fail;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.lenient;
//
//@ExtendWith(MockitoExtension.class)
//class RoleServiceTest {
//
//    private static MockWebServer mockBackEnd;
//
//    private static WebClient.Builder builder;
//
//    private static String baseUrl;
//
//    RoleServiceClient roleServiceInvoker;
//
//    @BeforeAll
//    static void setUp() throws IOException {
//        // this section uses a custom webclient props
//        final WebClientConfig config = new WebClientConfig();
//        config.setConnectTimeOutMs(500);
//        config.setReadTimeOutMs(1000);
//        builder = config.webClientBuilder();
//        mockBackEnd = new MockWebServer();
//        mockBackEnd.start();
//    }
//
//    @Mock
//    AzureTokenService mockTokenService;
//
//    @BeforeEach
//    void setUpEach() throws IOException {
//        mockBackEnd = new MockWebServer();
//        mockBackEnd.start();
//        baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
//        lenient().when(mockTokenService.getToken()).thenReturn("123ert");
//        roleServiceInvoker = new RoleServiceClient(builder, baseUrl, config. new ClientRoutesConfig());
//    }
//
//    @AfterAll
//    static void tearDown() throws IOException {
//        mockBackEnd.shutdown();
//    }
//
//    @Test
//    void testRoleService_WithRoleNoPermissions_WhenValidInput() throws IOException {
//        RoleDTO testRole = new RoleDTO();
//        testRole.setId("testId");
//        mockBackEnd.enqueue(new MockResponse()
//                .setBody(new ObjectMapper().writeValueAsString(testRole))
//               .addHeader("Content-Type", "application/json"));
//        RoleDTO returnedRoleId = roleServiceInvoker.createEntity(testRole);
//        assertNotNull(returnedRoleId);
//    }
//
//    @Test
//    void whenDependentServiceFailsWhenNull_CorrectException() {
//        assertThrows(Exception.class, () -> roleServiceInvoker.execute(null));
//    }
//
//    @Test
//    void whenDependentServiceFails_CorrectException() {
//        RoleDTO testRole = new RoleDTO();
//        testRole.setId("testId");
//
//        mockBackEnd.enqueue(new MockResponse()
//                .setResponseCode(500));
//
//        List<RoleDTO> roles = Collections.singletonList(testRole);
//        assertThrows(DependentServiceException.class, () -> roleServiceInvoker.execute(roles));
//    }
//
//    @Test
//    void testRoleService_WithList_WhenValidInput() throws IOException {
//        RoleDTO testRole = new RoleDTO();
//        testRole.setId("testId");
//
//        PermissionDTO testPermission = new PermissionDTO();
//        testPermission.setId("testId");
//
//        testRole.setPermissions(Collections.singletonList(testPermission));
//
//        List<RoleDTO> roles = Collections.singletonList(testRole);
//        mockBackEnd.enqueue(new MockResponse()
//                .setBody(new ObjectMapper().writeValueAsString(testRole))
//                .addHeader("Content-Type", "application/json"));
//        try {
//            roleServiceInvoker.execute(roles);
//        } catch(Exception e) {
//            fail("Should not have thrown any exception");
//        }
//    }
//}
