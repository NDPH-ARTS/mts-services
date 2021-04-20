package uk.ac.ox.ndph.mts.hello_world_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * An implementation to access secrets from KeyVault.
 */
@Component
public class KeyVaultConfigService implements ConfigService {

    @Value("${mySecret:keyvault not configured}")
    private final String mySecret = "defaultValue";

    @Override
    public String getSecret() {
        return mySecret;
    }
}
