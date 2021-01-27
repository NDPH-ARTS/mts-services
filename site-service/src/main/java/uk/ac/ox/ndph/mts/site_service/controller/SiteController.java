package uk.ac.ox.ndph.mts.site_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ox.ndph.mts.site_service.model.Site;
import uk.ac.ox.ndph.mts.site_service.service.SiteService;

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
     *
     * @param site
     * @return ResponseEntity
     */
    @PostMapping()
    @ResponseBody
    public String site(@RequestBody Site site) {
        return siteService.save(site);
    }
}
