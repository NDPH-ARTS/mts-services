package uk.ac.ox.ndph.mts.sample_service.client.practitioner_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.sample_service.client.Response;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.sample_service.exception.RestException;

import java.util.Arrays;
import java.util.List;

/**
 * Practitioner service client implementation
 */
@Service
public class PractitionerServiceClientImpl implements PractitionerServiceClient {

    private final WebClient webClient;

    private static final String SERVICE_NAME = "practitioner-service";

    private static final String ROLE_ASSIGNMENT_ROUTE = "/practitioner/roles";

    public PractitionerServiceClientImpl(final WebClient.Builder webClientBuilder,
                                         @Value("${practitioner.service.url}") String roleAssignmentsUrl) {
        this.webClient = webClientBuilder.baseUrl(roleAssignmentsUrl).build();
    }

    /**
     * Get practitioner role assignments by user id
     * @param userId - user id on the token
     * @return  List<RoleAssignmentDTO> list of role assignments
     */
    @Override
    public List<RoleAssignmentDTO> getUserRoleAssignments(String userId) {

        return webClient.get().uri(uriBuilder ->
                uriBuilder
                        .path(ROLE_ASSIGNMENT_ROUTE)
                        .queryParam("userIdentity", userId)
                        .build())
                .header("Content-Type", "application/json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(
                            new RestException(
                                        String.format(Response.CLIENT_ERROR_RESPONSE.message(),
                                                SERVICE_NAME, resp.statusCode(), userId))))
                .bodyToMono(RoleAssignmentDTO[].class)
                .map(Arrays::asList)
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }
}
