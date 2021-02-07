package uk.ac.ox.ndph.mts.sample_service.client.practitioner_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.sample_service.client.ClientResponse;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.RoleAssignmentDTO;
import uk.ac.ox.ndph.mts.sample_service.exception.RestException;

@Service
public class PractitionerServiceClientImpl implements PractitionerServiceClient {

    static final RestTemplate REST_TEMPLATE = new RestTemplate();

    private final WebClient webClient;

    private final String assignmentRoleRoute;

    private static final String SERVICE_NAME = "practitioner-service";

    public PractitionerServiceClientImpl(final WebClient.Builder webClientBuilder,
                                         @Value("${practitioner.service.url}") String assignmentRolesUrl) {
        this.webClient = webClientBuilder.baseUrl(assignmentRolesUrl).build();
        this.assignmentRoleRoute = "/practitioner/roles";
    }

    /**
     * Get participant roles
     * @param userId - user id on the token
     * @return true - currently it is a stub/infra until the implementation will be added
     */
    @Override
    public RoleAssignmentDTO[] getUserAssignmentRoles(String userId) {

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
                .bodyToMono(RoleAssignmentDTO[].class)
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }
}
