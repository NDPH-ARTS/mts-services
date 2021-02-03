package uk.ac.ox.ndph.mts.practitioner_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SiteServiceClient extends AbstractEntityServiceClient {

    public SiteServiceClient(final WebClient.Builder webClientBuilder,
                             @Value("${site.service.uri}") String serviceUrlBase) {
        this.serviceUrlBase = serviceUrlBase;
        this.webClient = webClientBuilder.baseUrl(serviceUrlBase).build();
        this.serviceExistsRoute = "/sites/{siteId}";
    }

}
