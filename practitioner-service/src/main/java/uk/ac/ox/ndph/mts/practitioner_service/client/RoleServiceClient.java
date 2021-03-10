package uk.ac.ox.ndph.mts.practitioner_service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.security.authentication.SecurityContextUtil;

@Component
public class RoleServiceClient extends AbstractEntityServiceClient {

    @Autowired
    public RoleServiceClient(final WebClient.Builder webClientBuilder,
                             @Value("${role.service.uri}") String serviceUrlBase,
                             SecurityContextUtil securityContextUtil) {
        this.serviceUrlBase =  serviceUrlBase;
        this.webClient = webClientBuilder.baseUrl(serviceUrlBase).build();
        this.serviceExistsRoute = "/roles/{id}";
        this.securityContextUtil = securityContextUtil;
    }
    

}
