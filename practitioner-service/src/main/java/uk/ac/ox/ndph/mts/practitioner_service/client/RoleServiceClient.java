package uk.ac.ox.ndph.mts.practitioner_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RoleServiceClient extends AbstractEntityServiceClient {

    public RoleServiceClient(final WebClient.Builder webClientBuilder,
                             @Value("${role.service.uri}") String serviceUrlBase) {
        super(webClientBuilder);
        this.serviceUrlBase = serviceUrlBase;
        this.serviceExistsRoute = "/roles/{id}";
    }

}
