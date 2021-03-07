package uk.ac.ox.ndph.mts.security.authentication;

import com.azure.spring.autoconfigure.aad.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({com.azure.spring.autoconfigure.aad.UserPrincipal.class, org.springframework.security.core.Authentication.class})
class SecurityContextUtilTests {

    UserPrincipal userPrincipalMock = mock(UserPrincipal.class);
    Authentication authenticationMock = mock(Authentication.class);

    SecurityContextUtil securityContextUtil;

    private final static String oidValue = "12345";

    @BeforeEach
    void setup() {
        securityContextUtil = new SecurityContextUtil();
    }

    @Test
    void TestSecurityId () {
        when(authenticationMock.getPrincipal()).thenReturn(userPrincipalMock);
        when(userPrincipalMock.getClaim("oid")).thenReturn(oidValue);
        SecurityContextHolder.getContext().setAuthentication(authenticationMock);

        assertTrue(securityContextUtil.getUserId().equals(oidValue));
    }

}
