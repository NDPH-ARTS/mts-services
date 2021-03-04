package uk.ac.ox.ndph.mts.practitioner_service;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import uk.ac.ox.ndph.mts.security.authorisation.AuthorisationService;

import static org.mockito.ArgumentMatchers.anyString;

@Profile("no-authZ")
@Configuration
public class TestAuthorisationConfigurationProvider {

    @Bean
    @Primary
    public AuthorisationService authorisationService() {
        var mockService = Mockito.mock(AuthorisationService.class);
        Mockito.when(mockService.authorise(anyString())).thenReturn(true);
        return mockService;
    }
}
