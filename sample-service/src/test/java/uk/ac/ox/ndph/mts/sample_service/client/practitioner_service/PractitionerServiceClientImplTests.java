package uk.ac.ox.ndph.mts.sample_service.client.practitioner_service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.sample_service.client.TestServiceBackend;
import uk.ac.ox.ndph.mts.sample_service.client.WebClientConfig;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.AssignmentRoleDTO;
import uk.ac.ox.ndph.mts.sample_service.exception.RestException;
import java.net.HttpURLConnection;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {"spring.cloud.config.enabled=false", "spring.main.allow-bean-definition-overriding=true"})
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
    void TestGetUserAssignmentRoles_WithValidResponse_ReturnsRoleAssignmentsAsExpected() {

        // Arrange

        AssignmentRoleDTO expectedRoleAssignment = new AssignmentRoleDTO();
        expectedRoleAssignment.setRoleId("roleId");
        expectedRoleAssignment.setSiteId("siteId");
         AssignmentRoleDTO[] expectedResponse = {expectedRoleAssignment};

        final var userId = "userID";
        String expectedBodyResponse =
                "[{\"practitionerId\":\"practitionerId\",\"siteId\":\"siteId\",\"roleId\":\"roleId\"}]";
        mockBackEnd.queueResponse(expectedBodyResponse);

        AssignmentRoleDTO[] expectedRoleAssignments = { expectedRoleAssignment };

        // Act
        AssignmentRoleDTO[] actualResponse = client.getUserAssignmentRoles(userId);

        //Assert
        assertAll(
                () -> assertEquals(expectedResponse.length, actualResponse.length ),
                () -> assertEquals(expectedResponse[0].getRoleId() , actualResponse[0].getRoleId()),
                () -> assertEquals(expectedResponse[0].getSiteId() , actualResponse[0].getSiteId())
        );
    }

    @Test
    void TestGetUserAssignmentRoles_WhenServiceFails_ThrowsRestException() {
        // Arrange
        this.mockBackEnd.queueErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        // Act
        // Assert
        assertThrows(RestException.class, () -> client.getUserAssignmentRoles("userId"));
    }

}
