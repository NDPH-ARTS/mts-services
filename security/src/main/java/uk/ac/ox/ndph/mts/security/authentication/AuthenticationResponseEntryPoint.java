package uk.ac.ox.ndph.mts.security.authentication;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AAD AuthenticationEntryPoint is used to set necessary response headers and content
 * on the response before sending it back to the client.
 */
@ControllerAdvice
class AuthenticationResponseEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

    /**
     * Commence unauthorised authentication response
     * @param request - http request
     * @param response - http response
     * @param auth - authentication exception
     * @throws IOException - signals that an I/O exception of some sort has occurred
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException auth) throws IOException {
        // 401
        setResponseError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed");
    }

    /**
     * Commence forbidden access response
     * @param request - http request
     * @param response - http response
     * @param accessDeniedException - denied eccess exception
     * @throws IOException - signals that an I/O exception of some sort has occurred
     */
    @ExceptionHandler(value = {AccessDeniedException.class})
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AccessDeniedException accessDeniedException) throws IOException {
        // 403
        setResponseError(response,
                HttpServletResponse.SC_FORBIDDEN,
                String.format("Access Denied: %s", accessDeniedException.getMessage()));
    }

    /**
     * Set the status code and the content on the http response
     * @param response - http response to set the values on
     * @param errorCode - http status code
     * @param errorMessage - error message content
     * @throws IOException - signals that an I/O exception of some sort has occurred
     */
    private void setResponseError(HttpServletResponse response,
                                  int errorCode,
                                  String errorMessage) throws IOException {
        response.setStatus(errorCode);
        response.getWriter().write(errorMessage);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
