package uk.ac.ox.ndph.mts.init_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import uk.ac.ox.ndph.mts.init_service.config.AzureTokenService;
import uk.ac.ox.ndph.mts.roleserviceclient.ResponseMessages;
import uk.ac.ox.ndph.mts.siteserviceclient.SiteServiceClient;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteDTO;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

@Service
public class SiteServiceInvoker {

    private SiteServiceClient siteServiceClient;
    private AzureTokenService azureTokenService;

    @Autowired
    public SiteServiceInvoker(final SiteServiceClient siteServiceClient,
                              AzureTokenService azureTokenservice) {
        this.siteServiceClient = siteServiceClient;
        this.azureTokenService = azureTokenservice;
    }

    public List<String> createManySites(final List<? extends SiteDTO> entities) {
        Objects.requireNonNull(entities, ResponseMessages.LIST_NOT_NULL);
        Consumer<HttpHeaders> authHeaders = SiteServiceClient.bearerAuth(azureTokenService.getToken());
        return entities.stream().map(s -> (siteServiceClient.createEntity(s, authHeaders))
                                                            .getId()).collect(toList());
    }
}
