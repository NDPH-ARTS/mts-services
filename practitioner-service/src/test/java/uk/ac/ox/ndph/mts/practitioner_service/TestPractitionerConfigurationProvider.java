package uk.ac.ox.ndph.mts.practitioner_service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerAttributeConfiguration;
import uk.ac.ox.ndph.mts.practitioner_service.model.PractitionerConfiguration;
import uk.ac.ox.ndph.mts.practitioner_service.configuration.PractitionerConfigurationProvider;

@Profile("test-all-required")
@Primary
@Configuration
public class TestPractitionerConfigurationProvider {
    
    private static List<PractitionerAttributeConfiguration> ALL_REQUIRED_UNDER_35_MAP = List.of(
        new PractitionerAttributeConfiguration("givenName", "Given Name", "^[a-zA-Z]{1,35}$"),
        new PractitionerAttributeConfiguration("familyName", "Family Name", "^[a-zA-Z]{1,35}$"),
        new PractitionerAttributeConfiguration("prefix", "Prefix", "^[a-zA-Z]{1,35}$"));

    @Bean
    @Primary
    public PractitionerConfigurationProvider practitionerConfigurationProvider() {
        var mock = mock(PractitionerConfigurationProvider.class);
        when(mock.getConfiguration()).thenReturn(new PractitionerConfiguration("person",
            "Practitioner", ALL_REQUIRED_UNDER_35_MAP)); 
        return mock;
    }
}
