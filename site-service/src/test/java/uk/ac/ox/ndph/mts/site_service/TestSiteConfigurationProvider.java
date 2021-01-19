package uk.ac.ox.ndph.mts.site_service;

import static org.mockito.Mockito.when;
import java.util.List;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import uk.ac.ox.ndph.mts.site_service.model.SiteAttributeConfiguration;
import uk.ac.ox.ndph.mts.site_service.model.SiteConfiguration;
import uk.ac.ox.ndph.mts.site_service.configuration.SiteConfigurationProvider;

@Profile("test-all-required")
@Primary
@Configuration
public class TestSiteConfigurationProvider {
    
    private static List<SiteAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP = List.of(
        new SiteAttributeConfiguration("name", "Name", "^[a-zA-Z]{1,35}$"),
        new SiteAttributeConfiguration("alias", "Alias", "^[a-zA-Z]{1,35}$"));

    @Bean
    @Primary
    public SiteConfigurationProvider practitionerConfigurationProvider() {
        var mock = Mockito.mock(SiteConfigurationProvider.class);
        when(mock.getConfiguration()).thenReturn(new SiteConfiguration("person",
            "Site", ALL_REQUIRED_UNDER_35_MAP)); 
        return mock;
    }
}
