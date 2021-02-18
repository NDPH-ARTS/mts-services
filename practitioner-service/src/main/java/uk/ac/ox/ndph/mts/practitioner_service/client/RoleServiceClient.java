package uk.ac.ox.ndph.mts.practitioner_service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RoleServiceClient extends AbstractEntityServiceClient {

    @Autowired
    public RoleServiceClient(WebClient.Builder webClientBuilder) {
        this(webClientBuilder, "http://role-service");
    }

    public RoleServiceClient(WebClient.Builder webClientBuilder, String serviceUrlBase) {
        this.serviceUrlBase =  serviceUrlBase;
        this.webClient = webClientBuilder.baseUrl(serviceUrlBase).build();
        this.serviceExistsRoute = "/roles/{id}";
    }
}
