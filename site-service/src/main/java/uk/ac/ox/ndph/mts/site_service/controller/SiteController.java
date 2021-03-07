package uk.ac.ox.ndph.mts.site_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ox.ndph.mts.site_service.model.Response;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.service.SiteService;

import java.util.List;

/**
 * Controller for site endpoint of site-service
 */
@RestController
@RequestMapping("/sites")
public class SiteController {

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
     * Try and create a posted new site
     * @param site site to post from request
     * @return ResponseEntity CREATED if successful plus returns the site ID
     */
    @PostMapping()
    public ResponseEntity<Response> site(@RequestBody Site site) {
        String siteId = siteService.save(site);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(siteId));
    }

    /**
     * Get complete sites list
     * @return ResponseEntity
     */
    @PostAuthorize("@authorisationService.filterMySites(returnObject)")
    @GetMapping
    public ResponseEntity<List<Site>> sites() {
        return ResponseEntity.status(HttpStatus.OK).body(siteService.findSites());
    }

    /**
     * Get site by id
     * @param id site id to search for
     * @return ResponseEntity
     */
    @GetMapping("/{id}")
    public ResponseEntity<Site> findSiteById(@PathVariable String id) {
        return ResponseEntity.ok(siteService.findSiteById(id));
    }

}
