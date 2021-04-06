package uk.ac.ox.ndph.mts.roleserviceclient.common;

import uk.ac.ox.ndph.mts.roleserviceclient.RequestExecutorRole;
import uk.ac.ox.ndph.mts.roleserviceclient.RoleServiceClient;
import uk.ac.ox.ndph.mts.roleserviceclient.configuration.WebClientConfigRole;

public class TestClientBuilder {

    private final WebClientConfigRole config;

    public TestClientBuilder() {
        this.config = new WebClientConfigRole();
        config.setConnectTimeOutMs(500);
        config.setReadTimeOutMs(1000);
        config.setInitialRetryDurationMs(500);
        config.setMaxRetryAttempts(0);
        config.setMaxRetryDurationMs(1000);
    }

    public RoleServiceClient build(final String url) {
        return new RoleServiceClient(config.webClientBuilderRole(),
                url,
                new RequestExecutorRole(config.retryPolicy()));
    }

}
