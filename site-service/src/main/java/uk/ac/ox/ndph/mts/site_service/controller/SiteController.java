package uk.ac.ox.ndph.mts.site_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ox.ndph.mts.site_service.model.Response;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.service.SiteService;

/**
 * Controller for site endpoint of site-service
 */
@RestController
public class SiteController {

    private static final String ENDPOINT_PATH = "/sites";
    private static final String APPLICATION_JSON = "application/json";

    private final SiteService siteService;

    /**
     *
     * @param siteService validate and save the site
     */
    @Autowired
    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    /**
     *
     * @param site
     * @return ResponseEntity
     */
    @PostMapping(path = ENDPOINT_PATH, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public ResponseEntity<Response> site(@RequestBody Site site) {
        String siteId = siteService.save(site);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(siteId));
    }
}