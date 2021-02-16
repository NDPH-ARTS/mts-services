package uk.ac.ox.ndph.mts.client.practitioner_service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.client.TestServiceBackend;
import uk.ac.ox.ndph.mts.client.WebClientConfig;
import uk.ac.ox.ndph.mts.client.dtos.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.security.exception.RestException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ContextConfiguration
class PractitionerServiceClientImplTests {

    public static TestServiceBackend mockBackEnd;

    private PractitionerServiceClientImpl client;

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
        this.client = new PractitionerServiceClientImpl(builder, mockBackEnd.getUrl());
    }

    @AfterEach
    void cleanup() {
        mockBackEnd.shutdown();
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
        mockBackEnd.queueResponse(expectedBodyResponse);

        // Act
        List<RoleAssignmentDTO> actualResponse = client.getUserRoleAssignments(userId);

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
        this.mockBackEnd.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        // Act
        // Assert
        assertThrows(RestException.class, () -> client.getUserRoleAssignments("userId"));
    }

}
