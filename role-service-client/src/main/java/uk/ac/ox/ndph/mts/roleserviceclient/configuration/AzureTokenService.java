package uk.ac.ox.ndph.mts.roleserviceclient.configuration;

import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AzureTokenService {

    public String getToken() {
        TokenCredential tokenCredential = new DefaultAzureCredentialBuilder().build();

        TokenRequestContext trc1 = new TokenRequestContext();
        // Ask for a basic scope that usually all users are able to get.
        trc1.addScopes("api://mts-dev/default");

        return Objects.requireNonNull(tokenCredential.getToken(trc1).block()).getToken();
    }

}
