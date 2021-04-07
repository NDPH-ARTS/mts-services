package uk.ac.ox.ndph.mts.practitionerserviceclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ox.ndph.mts.practitionerserviceclient.configuration.ClientRoutesConfigPractitioner;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.PractitionerDTO;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.PractitionerUserAccountDTO;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.ResponseDTO;
import uk.ac.ox.ndph.mts.practitionerserviceclient.model.RoleAssignmentDTO;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Service
public class PractitionerServiceClient {

    private final WebClient webClient;

    private final RequestExecutorPractitioner requestExecutor;

    @Autowired
    public PractitionerServiceClient(WebClient.Builder webClientBuilder,
                                     @Value("${practitioner.service.uri}")
                                     String practitionerServiceUri,
                                     RequestExecutorPractitioner requestExecutor) {
        this.requestExecutor = requestExecutor;
        this.webClient = webClientBuilder.baseUrl(practitionerServiceUri).build();
    }

    public static Consumer<HttpHeaders> bearerAuth(final String token) {
        return (headers) -> headers.setBearerAuth(token);
    }

    public List<RoleAssignmentDTO> getUserRoleAssignments(final String userId,
                                                          final Consumer<HttpHeaders> authHeaders) {
        String uri = UriComponentsBuilder
                .fromUriString(ClientRoutesConfigPractitioner.getServiceGetRoleAssignment())
                .queryParam("userIdentity", userId)
                .build().toString();
        return Arrays.asList(requestExecutor.sendBlockingGetRequest(webClient,
                                                                    uri, RoleAssignmentDTO[].class, authHeaders));

    }

    public ResponseDTO createEntity(final PractitionerDTO practitioner,
                                    final Consumer<HttpHeaders> authHeaders) {
        Objects.requireNonNull(practitioner, ResponseMessages.PRACTITIONER_NOT_NULL);
        return requestExecutor.sendBlockingPostRequest(webClient,
                ClientRoutesConfigPractitioner.getServiceCreatePractitioner(),
                practitioner, ResponseDTO.class, authHeaders);
    }

    public ResponseDTO assignRoleToPractitioner(final RoleAssignmentDTO roleAssignment,
                                                final Consumer<HttpHeaders> authHeaders) {
        String uri = UriComponentsBuilder
                .fromUriString(ClientRoutesConfigPractitioner.getServiceAssignRole())
                .build(roleAssignment.getPractitionerId())
                .toString();
        return requestExecutor.sendBlockingPostRequest(webClient, uri, roleAssignment, ResponseDTO.class, authHeaders);
    }

    public ResponseDTO linkUserAccount(final PractitionerUserAccountDTO userAccount,
                                       final Consumer<HttpHeaders> authHeaders) {
        String uri = UriComponentsBuilder
                .fromUriString(ClientRoutesConfigPractitioner.getServiceLinkUserAccount())
                .build(userAccount.getPractitionerId())
                .toString();
        return requestExecutor.sendBlockingPostRequest(webClient, uri, userAccount, ResponseDTO.class, authHeaders);
    }
}
