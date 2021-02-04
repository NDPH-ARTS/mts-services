package uk.ac.ox.ndph.mts.init_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.exception.NullEntityException;
import uk.ac.ox.ndph.mts.init_service.model.Entity;
import uk.ac.ox.ndph.mts.init_service.model.IDResponse;
import uk.ac.ox.ndph.mts.init_service.model.Role;
import uk.ac.ox.ndph.mts.init_service.model.Site;

import java.util.ArrayList;
import java.util.List;

@Service
public class SiteServiceInvoker extends ServiceInvoker {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteServiceInvoker.class);

    @Value("${site.service}")
    private String siteService;


    public SiteServiceInvoker() {
        this.webClient = WebClient.create(siteService);
    }
    public SiteServiceInvoker(WebClient webClient) {
        this.webClient = webClient;
    }

    protected String create(Entity site) throws DependentServiceException {

        try {
            IDResponse responseFromSiteService = sendBlockingPostRequest(siteService + "/sites", site, IDResponse.class);
            return responseFromSiteService.getId();

        } catch (Exception e) {
            LOGGER.info("FAILURE siteService {}", e.getMessage());
            throw new DependentServiceException("Error connecting to site service");
        }
    }

    public List<String> execute(List<Site> sites) throws NullEntityException {
        List<String> siteIDs = new ArrayList<>();
        if (sites != null) {
            for (Site site : sites) {
                LOGGER.info("Starting to create site(s): {}", sites);
                siteIDs.add(create(site));
                LOGGER.info("Finished creating {} site(s)", sites.size());
            }
        } else {
            throw new NullEntityException("No Sites in payload");
        }
        return siteIDs;
    }
}
