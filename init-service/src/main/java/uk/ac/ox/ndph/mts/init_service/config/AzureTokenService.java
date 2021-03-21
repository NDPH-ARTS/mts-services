package uk.ac.ox.ndph.mts.init_service.config;

import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.identity.ManagedIdentityCredential;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AzureTokenService implements TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureTokenService.class);

    @Override
    public String getToken() {
        TokenCredential tokenCredential = new DefaultAzureCredentialBuilder()
                .build();

        TokenRequestContext trc1 = new TokenRequestContext();
        // Ask for a basic scope that usually all users are able to get.
        trc1.addScopes("api://fa5cde1d-d6f8-4d13-9fa4-4d7a374cb290/.default");

        return Objects.requireNonNull(tokenCredential.getToken(trc1).block()).getToken();
    }

}
