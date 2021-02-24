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

@Component
public class AzureTokenService implements TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureTokenService.class);

    @Override
    public String getToken() {
        TokenCredential tokenCredential = new DefaultAzureCredentialBuilder().build();

        TokenRequestContext trc1 = new TokenRequestContext();
        trc1.addScopes("https://graph.microsoft.com/.default");

        String token = tokenCredential.getToken(trc1).block().getToken();

        LOGGER.info("Token is - " + token);


        String identityId = null;
        LOGGER.info("Gonna get id from token");
        try {
            DecodedJWT jwt = JWT.decode(token);
            LOGGER.info("Decoded token " + jwt.getToken());
            LOGGER.info("Gonna try to get Id  ");
            identityId = jwt.getId();
        } catch (JWTDecodeException jwtDecodeException) {
            //invalid token
            LOGGER.info("Ooops weve got an invalid token");
        }

        LOGGER.info("OID of azure token is - " + identityId);

        return token;
    }

}
