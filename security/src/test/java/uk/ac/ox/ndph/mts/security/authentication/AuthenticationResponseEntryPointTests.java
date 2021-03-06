package uk.ac.ox.ndph.mts.security.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import javax.servlet.http.HttpServletResponse;

class AuthenticationResponseEntryPointTests {

    private AuthenticationResponseEntryPoint authenticationEntryPoint;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;


    @BeforeEach
    void setUp() {
        authenticationEntryPoint = new AuthenticationResponseEntryPoint();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void TestCommence_WithForbiddenAccess_ReturnsResponseWithForbiddenAccess() throws Exception {
        AccessDeniedException ex = new AccessDeniedException("access denied");

        authenticationEntryPoint.commence(request, response, ex);
        assertThat(response.getStatus(), equalTo(HttpServletResponse.SC_FORBIDDEN));
    }

    @Test
    void TestCommence_WithAuthenticationException_ReturnsResponseWithUnauthorisedAccess() throws Exception {
        TestAuthenticationException ex = new TestAuthenticationException("any exception in the system");

        authenticationEntryPoint.commence(request, response, ex);
        assertThat(response.getStatus(), equalTo(HttpServletResponse.SC_UNAUTHORIZED));
    }

    private class TestAuthenticationException extends AuthenticationException{

        public TestAuthenticationException(String msg) {
            super(msg);
        }
    }
}
