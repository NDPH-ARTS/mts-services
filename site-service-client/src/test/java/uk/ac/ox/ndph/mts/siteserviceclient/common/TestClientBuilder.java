package uk.ac.ox.ndph.mts.siteserviceclient.common;

import uk.ac.ox.ndph.mts.siteserviceclient.RequestExecutorSite;
import uk.ac.ox.ndph.mts.siteserviceclient.SiteServiceClient;
import uk.ac.ox.ndph.mts.siteserviceclient.configuration.WebClientConfig;

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

    public SiteServiceClient build(final String url) {
        return new SiteServiceClient(config.webClientBuilder(),
                url,
                new RequestExecutorSite(config.retryPolicy()));
    }

}
