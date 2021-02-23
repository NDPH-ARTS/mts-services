package uk.ac.ox.ndph.mts.init_service.config;

import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureTokenService implements TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureTokenService.class);

    @Override
    public String getToken() {
        TokenCredential tokenCredential = new DefaultAzureCredentialBuilder().build();

        TokenRequestContext trc1 = new TokenRequestContext();
        trc1.addScopes("https://graph.microsoft.com/.default");

        String token = tokenCredential.getToken(trc1).block().getToken();

        LOGGER.info("Token is - " + token);

        LOGGER.info("OID of azure token is - " + getIdentityId());

        return token;
    }

}
