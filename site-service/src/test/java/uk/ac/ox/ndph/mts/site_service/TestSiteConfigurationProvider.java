package uk.ac.ox.ndph.mts.site_service;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import uk.ac.ox.ndph.mts.site_service.configuration.SiteConfigurationProvider;
import uk.ac.ox.ndph.mts.site_service.model.SiteAttributeConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@Profile("test-all-required")
@Primary
@Configuration
public class TestSiteConfigurationProvider {
    
    private static List<SiteAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP = List.of(
        new SiteAttributeConfiguration("name", "Name", "^[a-zA-Z]{1,35}$"),
        new SiteAttributeConfiguration("alias", "Alias", "^[a-zA-Z]{1,35}$"),
        new SiteAttributeConfiguration("parentSiteId", "Parent Site Id", ""),
        new SiteAttributeConfiguration("siteType", "Site Type", ""));

    private static final List<SiteConfiguration> SITE_CONFIGURATION_LIST  = List.of(
            new SiteConfiguration("Organization", "site", "REGION", ALL_REQUIRED_UNDER_35_MAP,
                    Collections.singletonList(new SiteConfiguration("Organization", "site", "COUNTRY", ALL_REQUIRED_UNDER_35_MAP,
                            Collections.singletonList(new SiteConfiguration("Organization", "site", "LCC", ALL_REQUIRED_UNDER_35_MAP, null)
                            )))));

    @Bean
    @Primary
    public SiteConfigurationProvider siteConfigurationProvider() {
        var mock = Mockito.mock(SiteConfigurationProvider.class);
        when(mock.getConfiguration()).thenReturn(new SiteConfiguration("site",
                "Site", "CCO", ALL_REQUIRED_UNDER_35_MAP, SITE_CONFIGURATION_LIST));
        return mock;
    }
}
