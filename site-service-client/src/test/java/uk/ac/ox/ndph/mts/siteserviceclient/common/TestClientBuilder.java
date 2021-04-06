package uk.ac.ox.ndph.mts.siteserviceclient.common;

import uk.ac.ox.ndph.mts.siteserviceclient.RequestExecutorSite;
import uk.ac.ox.ndph.mts.siteserviceclient.SiteServiceClient;
import uk.ac.ox.ndph.mts.siteserviceclient.configuration.WebClientConfigSite;

public class TestClientBuilder {

    private final WebClientConfigSite config;

    public TestClientBuilder() {
        this.config = new WebClientConfigSite();
        config.setConnectTimeOutMs(500);
        config.setReadTimeOutMs(1000);
        config.setInitialRetryDurationMs(500);
        config.setMaxRetryAttempts(0);
        config.setMaxRetryDurationMs(1000);
    }

    public SiteServiceClient build(final String url) {
        return new SiteServiceClient(config.webClientBuilderSite(),
                url,
                new RequestExecutorSite(config.retryPolicy()));
    }

}
