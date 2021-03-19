package uk.ac.ox.ndph.mts.site_service;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import uk.ac.ox.ndph.mts.site_service.model.SiteAttributeConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;

import java.util.Collections;
import java.util.List;

@Profile("test-all-required")
@SpringBootConfiguration
public class TestSiteConfiguration {
    
    private static final List<SiteAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP = List.of(
        new SiteAttributeConfiguration("name", "string", "Name", "^[a-zA-Z]{1,35}$"),
        new SiteAttributeConfiguration("alias", "string","Alias", "^[a-zA-Z]{1,35}$"),
        new SiteAttributeConfiguration("parentSiteId", "string","Parent Site Id", ""),
        new SiteAttributeConfiguration("siteType","string", "Site Type", ""));

    private static final List<SiteAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP_CUSTOM = List.of(
            new SiteAttributeConfiguration("address", "address", "Address", ""));


    private static final List<SiteConfiguration> SITE_CONFIGURATION_LIST  = List.of(
            new SiteConfiguration("Organization", "site", "REGION", ALL_REQUIRED_UNDER_35_MAP, null,
                    Collections.singletonList(new SiteConfiguration("Organization", "site", "COUNTRY", ALL_REQUIRED_UNDER_35_MAP, null,
                            Collections.singletonList(new SiteConfiguration("Organization", "site", "LCC", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, null)
                            )))));

    // use this site config for all tests unless they define their own one
    @Primary
    @Bean
    public SiteConfiguration getSiteConfiguration() {
        return new SiteConfiguration("site",
                "Site", "CCO", ALL_REQUIRED_UNDER_35_MAP, ALL_REQUIRED_UNDER_35_MAP_CUSTOM, SITE_CONFIGURATION_LIST);
    }

}
