package uk.ac.ox.ndph.mts.init_service.config;

import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AzureTokenService implements TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureTokenService.class);

    @Value("${mts.scope}")
    private String scope;

    @Override
    public String getToken() {
        TokenCredential tokenCredential = new DefaultAzureCredentialBuilder()
                .build();

        TokenRequestContext trc1 = new TokenRequestContext();
        // Ask for a basic scope that usually all users are able to get.
        trc1.addScopes(scope);

        return Objects.requireNonNull(tokenCredential.getToken(trc1).block()).getToken();
    }

}
