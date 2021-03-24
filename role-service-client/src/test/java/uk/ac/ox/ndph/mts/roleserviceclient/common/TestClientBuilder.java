package uk.ac.ox.ndph.mts.roleserviceclient.common;

import uk.ac.ox.ndph.mts.roleserviceclient.RequestExecutor;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
import uk.ac.ox.ndph.mts.roleserviceclient.configuration.ClientRoutesConfig;
import uk.ac.ox.ndph.mts.roleserviceclient.configuration.WebClientConfig;

public class TestClientBuilder {

    private final WebClientConfig config;

    public TestClientBuilder() {
        this.config = new WebClientConfig();
        config.setConnectTimeOutMs(500);
        config.setReadTimeOutMs(1000);
        config.setInitialRetryDurationMs(500);
        config.setMaxRetryAttempts(0);
        config.setMaxRetryDurationMs(1000);
    }

    public RoleServiceClient build(final String url) {
        return new RoleServiceClient(config.webClientBuilder(),
                url,
                new ClientRoutesConfig(),
                new RequestExecutor(config.retryPolicy()));
    }

}
