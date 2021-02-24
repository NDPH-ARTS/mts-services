package uk.ac.ox.ndph.mts.init_service.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AzureTokenService implements TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureTokenService.class);

    @Override
    public String getToken() {
        TokenCredential tokenCredential = new DefaultAzureCredentialBuilder().build();

        TokenRequestContext trc1 = new TokenRequestContext();
        trc1.addScopes("https://graph.microsoft.com/.default");

        return Objects.requireNonNull(tokenCredential.getToken(trc1).block()).getToken();
    }

}
