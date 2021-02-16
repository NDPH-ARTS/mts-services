package uk.ac.ox.ndph.mts.client.practitioner_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.client.Response;
import uk.ac.ox.ndph.mts.client.dtos.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.security.exception.RestException;

import java.util.Arrays;
import java.util.List;

/**
 * Practitioner service client implementation
 */
@Service
public class PractitionerServiceClientImpl implements PractitionerServiceClient {

    private final WebClient webClient;

    @Value("${practitioner.service.name}")
    private String serviceName;

    @Value("${practitioner.service.endpoint.roles}")
    private String roleAssignmentRoute;

    public PractitionerServiceClientImpl(final WebClient.Builder webClientBuilder,
                                         @Value("${practitioner.service.url}") String roleAssignmentsUrl) {
        this.webClient = webClientBuilder.baseUrl(roleAssignmentsUrl).build();
    }

    /**
     * Get practitioner role assignments by user id
     * @param userId - user id on the token
     * @return list of role assignments
     */
    @Override
    public List<RoleAssignmentDTO> getUserRoleAssignments(String userId) {

        return webClient.get().uri(uriBuilder ->
                uriBuilder
                        .path(roleAssignmentRoute)
                        .queryParam("userIdentity", userId)
                        .build())
                .header("Content-Type", "application/json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(
                            new RestException(
                                        String.format(Response.CLIENT_ERROR_RESPONSE.message(),
                                                serviceName, resp.statusCode(), userId))))
                .bodyToMono(RoleAssignmentDTO[].class)
                .map(Arrays::asList)
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }
}
