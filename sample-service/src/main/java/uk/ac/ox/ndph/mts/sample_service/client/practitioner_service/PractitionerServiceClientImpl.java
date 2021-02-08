package uk.ac.ox.ndph.mts.sample_service.client.practitioner_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.sample_service.client.ClientResponse;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.AssignmentRoleDTO;
import uk.ac.ox.ndph.mts.sample_service.exception.RestException;

/**
 * Practitioner service client implementation
 */
@Service
public class PractitionerServiceClientImpl implements PractitionerServiceClient {

    private final WebClient webClient;

    private final String assignmentRoleRoute;

    private static final String SERVICE_NAME = "practitioner-service";

    public PractitionerServiceClientImpl(final WebClient.Builder webClientBuilder,
                                         @Value("${practitioner.service.url}") String assignmentRolesUrl) {
        this.webClient = webClientBuilder.baseUrl(assignmentRolesUrl).build();
        this.assignmentRoleRoute = "/practitioner/roles";
    }

    /**
     * Get practitioner assignment roles by user id
     * @param userId - user id on the token
     * @return  AssignmentRoleDTO[] array of assignment roles
     */
    @Override
    public AssignmentRoleDTO[] getUserAssignmentRoles(String userId) {

        return webClient.get().uri(uriBuilder ->
                uriBuilder
                        .path(assignmentRoleRoute)
                        .queryParam("userIdentity", userId)
                        .build())
                .header("Content-Type", "application/json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(
                            new RestException(
                                        String.format(ClientResponse.CLIENT_ERROR_RESPONSE.message(),
                                                SERVICE_NAME, resp.statusCode(), userId))))
                .bodyToMono(AssignmentRoleDTO[].class)
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }
}
