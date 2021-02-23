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

class AADAuthenticationEntryPointTests {

    private AADAuthenticationEntryPoint aadAuthenticationEntryPoint;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;


    @BeforeEach
    void setUp() {
        aadAuthenticationEntryPoint = new AADAuthenticationEntryPoint();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void TestCommence_WithForbiddenAccess_ReturnsResponseWithForbiddenAccess() throws Exception {
        AccessDeniedException ex = new AccessDeniedException("access denied");

        aadAuthenticationEntryPoint.commence(request, response, ex);
        assertThat(response.getStatus(), equalTo(HttpServletResponse.SC_FORBIDDEN));
    }

    @Test
    void TestCommence_WithException_ReturnsResponseWithInternalServerError() throws Exception {
        Exception ex = new Exception("any exception in the system");

        aadAuthenticationEntryPoint.commence(request, response, ex);
        assertThat(response.getStatus(), equalTo(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
    }

    @Test
    void TestCommence_WithAuthenticationException_ReturnsResponseWithUnauthorisedAccess() throws Exception {
        TestAuthenticationException ex = new TestAuthenticationException("any exception in the system");

        aadAuthenticationEntryPoint.commence(request, response, ex);
        assertThat(response.getStatus(), equalTo(HttpServletResponse.SC_UNAUTHORIZED));
    }

    private class TestAuthenticationException extends AuthenticationException{

        public TestAuthenticationException(String msg) {
            super(msg);
        }
    }
}
