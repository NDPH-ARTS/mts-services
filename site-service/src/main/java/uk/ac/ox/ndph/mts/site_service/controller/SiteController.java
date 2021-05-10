package uk.ac.ox.ndph.mts.site_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ox.ndph.mts.site_service.model.Response;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.model.SiteDTO;
import uk.ac.ox.ndph.mts.site_service.model.SiteNameDTO;
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
    public ResponseEntity<Response> createSite(@RequestBody Site site) {
        String siteId = siteService.save(site);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(siteId));
    }

    @PostAuthorize("@siteServiceImpl.filterUserSites(returnObject, #role, 'view-site')")
    @GetMapping
    public List<SiteDTO> getAssignedSites(@RequestParam(value = "role", required = false) String role) {
        return siteService.findSites();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Site> findSiteById(@PathVariable String id) {
        return ResponseEntity.ok(siteService.findSiteById(id));
    }

    @GetMapping("/parents/{id}")
    public List<String> getParentSiteIds(@PathVariable String id) {
        return siteService.findParentSiteIds(id);
    }

    @GetMapping("/assigned")
    public List<SiteNameDTO> getAssignedSites() {
        return siteService.findAssignedSites();
    }

}
