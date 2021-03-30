package uk.ac.ox.ndph.mts.site_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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


@RestController
@RequestMapping("/sites")
public class SiteController {

    private final SiteService siteService;

    @Autowired
    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @PreAuthorize("@authorisationService.authorise('create-site', #site.parentSiteId)")
    @PostMapping()
    public ResponseEntity<Response> site(@RequestBody Site site) {
        String siteId = siteService.save(site);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(siteId));
    }

    @GetMapping
    public ResponseEntity<List<Site>> sites() {
        return ResponseEntity.status(HttpStatus.OK).body(siteService.findSites());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Site> findSiteById(@PathVariable String id) {
        return ResponseEntity.ok(siteService.findSiteById(id));
    }

}
