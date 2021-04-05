package uk.ac.ox.ndph.mts.siteserviceclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ox.ndph.mts.siteserviceclient.configuration.ClientRoutesConfigSite;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteDTO;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Service
public class SiteServiceClient {

    private final WebClient webClient;

    private final RequestExecutorSite requestExecutor;

    @Autowired
    public SiteServiceClient(WebClient.Builder webClientBuilder,
                             @Value("${site.service.uri}") String siteServiceUri,
                             RequestExecutorSite requestExecutor) {
        this.requestExecutor = requestExecutor;
        this.webClient = webClientBuilder.baseUrl(siteServiceUri).build();
    }

    public static Consumer<HttpHeaders> noAuth() {
        return (headers) -> {
        };
    }

    public static Consumer<HttpHeaders> basicAuth(final String username, final String password) {
        return (headers) -> headers.setBasicAuth(username, password);
    }

    public static Consumer<HttpHeaders> bearerAuth(final String token) {
        return (headers) -> headers.setBearerAuth(token);
    }


    public boolean entityIdExists(final String siteId,
                                  final Consumer<HttpHeaders> authHeaders) {
        Objects.requireNonNull(siteId, ResponseMessages.ID_NOT_NULL);
        try {
            getById(siteId, authHeaders);
        } catch (RuntimeException ex) {
            if (ex.getCause().getMessage().contains("404")) {
                return false;
            }
            throw ex;
        }

        return true;
    }

    public SiteDTO createEntity(final SiteDTO site,
                                final Consumer<HttpHeaders> authHeaders) {
        Objects.requireNonNull(site, ResponseMessages.SITE_NOT_NULL);
        return requestExecutor.sendBlockingPostRequest(webClient,
                ClientRoutesConfigSite.getServiceCreateSite(),
                site, SiteDTO.class, authHeaders);
    }

    public List<SiteDTO> getAllSites(final Consumer<HttpHeaders> authHeaders) {
        String uri = UriComponentsBuilder
                .fromUriString(ClientRoutesConfigSite.getServiceGetAllSites())
                .build().toString();
        return Arrays.asList(requestExecutor.sendBlockingGetRequest(webClient, uri, SiteDTO[].class, authHeaders));
    }

    public SiteDTO getById
            (final String siteId,
             final Consumer<HttpHeaders> authHeaders) {
        Objects.requireNonNull(siteId, ResponseMessages.ID_NOT_NULL);
        String uri = UriComponentsBuilder
                .fromUriString(ClientRoutesConfigSite.getServiceGetSite())
                .build(siteId).toString();
        return requestExecutor.sendBlockingGetRequest(webClient, uri, SiteDTO.class, authHeaders);
    }

}
