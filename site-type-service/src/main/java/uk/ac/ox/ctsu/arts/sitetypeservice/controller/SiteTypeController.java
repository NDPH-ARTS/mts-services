package uk.ac.ox.ctsu.arts.sitetypeservice.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import uk.ac.ox.ctsu.arts.sitetypeservice.exception.NotFoundException;
import uk.ac.ox.ctsu.arts.sitetypeservice.model.SiteType;
import uk.ac.ox.ctsu.arts.sitetypeservice.model.SiteTypeRepository;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/sitetype")
public class SiteTypeController {
    private final SiteTypeRepository siteTypeRepository;

    public SiteTypeController(SiteTypeRepository siteTypeRepository) {
        this.siteTypeRepository = siteTypeRepository;
    }

    @GetMapping("/get/{id}")
    SiteType get(@PathVariable Long id) {
        return siteTypeRepository.findById(id).orElseThrow(() -> new NotFoundException());
    }

    @GetMapping("")
    Page<SiteType> getPaged(@RequestParam int page, @RequestParam int size) {
        return siteTypeRepository.findAll(PageRequest.of(page, size));
    }

    @PostMapping("/create")
    SiteType create(
        @RequestBody
            SiteType siteType, @AuthenticationPrincipal Jwt jwt) {
        siteType.setChangedWhen(LocalDateTime.now());
        siteType.setChangedWho(jwt.getClaimAsString("name"));
        return siteTypeRepository.save(siteType);
    }

    @PostMapping("/update")
    SiteType update(
        @RequestBody
            SiteType siteType) {
        return siteTypeRepository.save(siteType);
    }

    @GetMapping("/oidc-principal")
    public String getOidcUserPrincipal(@AuthenticationPrincipal Jwt jwt) {
        return String.format("Hello, %s!", jwt.getClaimAsString("name"));
    }
}
