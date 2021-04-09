package uk.ac.ox.ndph.mts.roleserviceclient;

import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import uk.ac.ox.ndph.mts.roleserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.roleserviceclient.common.TestClientBuilder;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FindPageTest {

    public static MockWebServerWrapper webServer;
    private static final TestClientBuilder builder = new TestClientBuilder();
    private RoleServiceClient roleServiceClient;
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
        roleServiceClient = builder.build(webServer.getUrl());
    }

    @AfterAll
    static void afterAll() {
        webServer.shutdown();
    }

    @Test
    void whenHttpSuccess_bodyReturnedAsPageOfRole() {
        final String responseBody = "{\"content\":[{\"createdDateTime\":\"2021-03-01T17:16:22.111163\"," +
            "\"createdBy\":\"fake-id\",\"modifiedDateTime\":\"2021-03-01T17:16:22.111163\",\"modifiedBy\":\"fake-id\"," +
            "\"id\":\"country-admin\",\"permissions\":[{\"createdDateTime\":null,\"createdBy\":\"katesan\"," +
            "\"modifiedDateTime\":null,\"modifiedBy\":\"katesan\",\"id\":\"create-person\"}]}]," +
            "\"pageable\":{\"sort\":{\"sorted\":false,\"unsorted\":true,\"empty\":true}," +
            "\"offset\":0,\"pageNumber\":0,\"pageSize\":5,\"paged\":true,\"unpaged\":false},\"totalPages\":1," +
            "\"totalElements\":2,\"last\":true,\"sort\":{\"sorted\":false,\"unsorted\":true,\"empty\":true}," +
            "\"first\":true,\"size\":5,\"number\":0,\"numberOfElements\":2,\"empty\":false}";
        webServer.queueResponse(responseBody);
        final Page<RoleDTO> actualResponse = roleServiceClient.getPage(0, 5, RoleServiceClient.bearerAuth(token));
        final List<RoleDTO> actualRoles = actualResponse.getContent();
        //Assert
        assertEquals(1, actualRoles.size());
        assertEquals("country-admin", actualRoles.get(0).getId());
        assertEquals(1, actualRoles.get(0).getPermissions().size());
        assertEquals("create-person", actualRoles.get(0).getPermissions().get(0).getId());
    }

    @Test
    void whenServiceError_thenThrowRestException() {
        // Arrange
        webServer.queueResponse(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        // Act + Assert
        assertThrows(Exception.class, () -> roleServiceClient.getPage(0, 5, RoleServiceClient.bearerAuth(token)));
    }

}
