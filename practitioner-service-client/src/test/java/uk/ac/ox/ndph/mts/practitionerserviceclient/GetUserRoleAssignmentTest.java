
package uk.ac.ox.ndph.mts.practitionerserviceclient;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import uk.ac.ox.ndph.mts.practitionerserviceclient.common.MockWebServerWrapper;
import uk.ac.ox.ndph.mts.practitionerserviceclient.common.TestClientBuilder;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.RoleAssignmentDTO;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class GetUserRoleAssignmentTest {

    public static MockWebServerWrapper webServer;
    private static final TestClientBuilder builder = new TestClientBuilder();
    private PractitionerServiceClient practitionerServiceClient;
    private String token = "some-token";
    private Consumer<HttpHeaders> authHeaders = PractitionerServiceClient.bearerAuth(token);

    @SpringBootApplication
    static class TestConfiguration {
    }

    @BeforeAll
    static void beforeAll() {
        webServer = MockWebServerWrapper.newStartedInstance();
    }

    @BeforeEach
    void beforeEach() {
        practitionerServiceClient = builder.build(webServer.getUrl());

    }

    @AfterAll
    static void afterAll() {
        webServer.shutdown();
    }

    @Test
    void TestGetUserRoleAssignments_WithValidResponse_ReturnsRoleAssignmentsAsExpected() {

        // Arrange

        RoleAssignmentDTO expectedRoleAssignment = new RoleAssignmentDTO();
        expectedRoleAssignment.setRoleId("roleId");
        expectedRoleAssignment.setSiteId("siteId");
        List<RoleAssignmentDTO> expectedResponse = Collections.singletonList(expectedRoleAssignment);

        final var userId = "userID";
        String expectedBodyResponse =
                "[{\"practitionerId\":\"practitionerId\",\"siteId\":\"siteId\",\"roleId\":\"roleId\"}]";
        webServer.queueResponse(expectedBodyResponse);

        // Act
        List<RoleAssignmentDTO> actualResponse = practitionerServiceClient.getUserRoleAssignments(userId, authHeaders);

        //Assert
        assertAll(
                () -> assertEquals(expectedResponse.size(), actualResponse.size() ),
                () -> assertEquals(expectedResponse.get(0).getRoleId() , actualResponse.get(0).getRoleId()),
                () -> assertEquals(expectedResponse.get(0).getSiteId() , actualResponse.get(0).getSiteId())
        );
    }

    @Test
    void TestGetUserRoleAssignments_WhenServiceFails_ThrowsRestException() {
        // Arrange
        webServer.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        // Act
        // Assert
        assertThrows(Exception.class, () -> practitionerServiceClient.getUserRoleAssignments("userId", authHeaders));
    }
}
