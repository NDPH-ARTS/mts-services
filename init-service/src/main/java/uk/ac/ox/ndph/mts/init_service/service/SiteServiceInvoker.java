package uk.ac.ox.ndph.mts.init_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ox.ndph.mts.init_service.config.AzureTokenService;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.model.Entity;
import uk.ac.ox.ndph.mts.init_service.model.IDResponse;

@Service
public class SiteServiceInvoker extends ServiceInvoker {
    @Value("${site-service.routes.create}")
    private String createEndpoint;

    @Autowired
    public SiteServiceInvoker(final WebClient.Builder webClientBuilder,
                              @Value("${site-service.uri}") String serviceUrlBase,
                              AzureTokenService azureTokenservice) {
        super(webClientBuilder, serviceUrlBase, azureTokenservice);
    }

    protected String create(Entity site) throws DependentServiceException {
        String uri = serviceUrlBase + createEndpoint;
        IDResponse responseFromSiteService = sendBlockingPostRequest(uri, site, IDResponse.class);
        return responseFromSiteService.getId();
    }
}
