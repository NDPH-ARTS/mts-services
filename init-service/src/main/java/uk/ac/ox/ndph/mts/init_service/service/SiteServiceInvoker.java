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
import uk.ac.ox.ndph.mts.init_service.model.Entity;
import uk.ac.ox.ndph.mts.init_service.model.Site;

import java.util.List;

@Service
public class SiteServiceInvoker implements ServiceInvoker {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteServiceInvoker.class);

    @Value("${site.service}")
    private String siteService;

    private final WebClient webClient;

    public SiteServiceInvoker() {
        this.webClient = WebClient.create(siteService);
    }
    public SiteServiceInvoker(WebClient webClient) {
        this.webClient = webClient;
    }

    public Site send(Entity site) throws DependentServiceException {

        try {
            return webClient.post()
                    .uri(siteService + "/sites")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(site), Site.class)
                    .retrieve()
                    .bodyToMono(Site.class)
                    .block();
        } catch (Exception e) {
            LOGGER.info("FAILURE siteService {}", e.getMessage());
            throw new DependentServiceException("Error connecting to site service");
        }
    }

    public void execute(List<Site> sites) throws Exception {
        if (sites != null) {
            for (Site site : sites) {
                send(site);
                LOGGER.info("Created: {}", site);
            }
        } else {
            throw new Exception();
        }
    }
}
