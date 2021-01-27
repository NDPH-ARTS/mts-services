package uk.ac.ox.ndph.mts.sample_service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class AuthorisationServiceTests {

    private AuthorisationService authorisationService;

    @BeforeEach
    void setUp() {
        authorisationService = new AuthorisationService();
    }

    @Test
    void TestAuthorise_WithForbiddenPermission_ReturnsFalse(){
        AuthorisationService authorisationService = new AuthorisationService();
        assertThat(authorisationService.authorise("unauthorisedPermission"), equalTo(false));
    }
    @Test
    void TestAuthorise_WithAllowedPermission_ReturnsTrue(){
        AuthorisationService authorisationService = new AuthorisationService();
        assertThat(authorisationService.authorise("stubPermission"), equalTo(true));
    }
}
