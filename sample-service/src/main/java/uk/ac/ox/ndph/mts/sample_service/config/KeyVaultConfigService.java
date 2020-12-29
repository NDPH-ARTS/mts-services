package uk.ac.ox.ndph.mts.sample_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyVaultConfigService implements ConfigService {

    @Value("${mySecret:keyvault not configured}")
    private String mySecret = "defaultValue";

    @Override
    public String getSecret() {
        return mySecret;
    }
}
