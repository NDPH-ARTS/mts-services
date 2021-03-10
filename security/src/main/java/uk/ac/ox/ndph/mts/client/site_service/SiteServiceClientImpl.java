package uk.ac.ox.ndph.mts.client.site_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.client.Response;
import uk.ac.ox.ndph.mts.client.dtos.SiteDTO;
import uk.ac.ox.ndph.mts.security.authentication.SecurityContextUtil;
import uk.ac.ox.ndph.mts.security.exception.RestException;

import java.util.Arrays;
import java.util.List;

/**
 * Site service client implementation
 */
@Service
public class SiteServiceClientImpl implements SiteServiceClient {

    private final WebClient webClient;

    private final SecurityContextUtil securityContextUtil;

    @Value("${site.service.name}")
    private String serviceName;

    @Value("${site.service.endpoint.sites}")
    private String sitesRoute;

    @Autowired
    public SiteServiceClientImpl(final WebClient.Builder webClientBuilder,
                                 @Value("${site.service.uri}") String assignmentRolesUrl,
                                 SecurityContextUtil securityContextUtil) {
        this.webClient = webClientBuilder.baseUrl(assignmentRolesUrl).build();
        this.securityContextUtil = securityContextUtil;
    }

    /**
     * Get all sites
     * @return  list of SiteDTO
     */
    @Override
    public List<SiteDTO> getAllSites() {

        return webClient.get().uri(sitesRoute)
                .headers( (headers) -> headers.setBearerAuth(securityContextUtil.getToken()) )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(httpStatus -> !httpStatus.is2xxSuccessful(),
                    resp -> Mono.error(
                            new RestException(
                                        String.format(Response.CLIENT_ERROR_RESPONSE.message(),
                                                serviceName, resp.statusCode(), sitesRoute))))
                .bodyToMono(SiteDTO[].class)
                .map(Arrays::asList)
                .onErrorResume(e -> Mono.error(new RestException(e.getMessage(), e)))
                .block();
    }
}
