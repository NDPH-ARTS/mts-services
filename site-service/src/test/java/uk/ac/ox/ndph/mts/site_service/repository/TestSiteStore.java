package uk.ac.ox.ndph.mts.site_service.repository;

import uk.ac.ox.ndph.mts.site_service.model.Site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Fake in-memory store for testing only
 */
public class TestSiteStore implements EntityStore<Site, String> {

    private final List<Site> sites = new ArrayList<>();

    @Override
    public String saveEntity(Site site) {
        final Site copy = new Site(UUID.randomUUID().toString(), site.getName(), site.getAlias(), site.getParentSiteId(), site.getSiteType());
        this.sites.add(copy);
        return copy.getSiteId();
    }

    @Override
    public List<Site> findAll() {
        return Collections.unmodifiableList(sites);
    }

    @Override
    public Optional<Site> findByName(final String name) {
        return sites.stream().filter(site -> site.getName().equals(name)).findFirst();
    }

    @Override
    public Optional<Site> findById(final String id) {
        return sites.stream().filter(site -> site.getSiteId().equals(id)).findFirst();
    }

    @Override
    public Optional<Site> findRoot() {
        return sites.stream().filter(site -> site.getParentSiteId() == null).findFirst();
    }
}
