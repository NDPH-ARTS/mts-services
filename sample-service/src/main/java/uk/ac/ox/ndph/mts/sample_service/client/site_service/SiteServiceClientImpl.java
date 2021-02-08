package uk.ac.ox.ndph.mts.sample_service.client.site_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.sample_service.client.ClientResponse;
import uk.ac.ox.ndph.mts.sample_service.client.dtos.SiteDTO;
import uk.ac.ox.ndph.mts.sample_service.exception.RestException;

/**
 * Site service client implementation
 */
@Service
public class SiteServiceClientImpl implements SiteServiceClient {

    private final WebClient webClient;

    private final String sitesRoute;

    private static final String SERVICE_NAME = "site-service";

    public SiteServiceClientImpl(final WebClient.Builder webClientBuilder,
                                 @Value("${site.service.url}") String assignmentRolesUrl) {
        this.webClient = webClientBuilder.baseUrl(assignmentRolesUrl).build();
        this.sitesRoute = "/sites";
    }

    /**
     * Get all sites
     * @return  SiteDTO[] array of sites
     */
    @Override
    public SiteDTO[] getAllSites() {

        return webClient.get().uri(sitesRoute)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(
                            new RestException(
                                        String.format(ClientResponse.CLIENT_ERROR_RESPONSE.message(),
                                                SERVICE_NAME, resp.statusCode(), sitesRoute))))
                .bodyToMono(SiteDTO[].class)
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }
}
