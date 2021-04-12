package uk.ac.ox.ndph.mts.practitionerserviceclient.common;

import uk.ac.ox.ndph.mts.practitionerserviceclient.PractitionerServiceClient;
import uk.ac.ox.ndph.mts.practitionerserviceclient.RequestExecutorPractitioner;
import uk.ac.ox.ndph.mts.practitionerserviceclient.configuration.WebClientConfigPractitioner;

public class TestClientBuilder {

    private final WebClientConfigPractitioner config;

    public TestClientBuilder() {
        this.config = new WebClientConfigPractitioner();
        config.setConnectTimeOutMs(500);
        config.setReadTimeOutMs(1000);
        config.setInitialRetryDurationMs(500);
        config.setMaxRetryAttempts(0);
        config.setMaxRetryDurationMs(1000);
    }

    public PractitionerServiceClient build(final String url) {
        return new PractitionerServiceClient(config.webClientBuilderPractitioner(),
                url,
                new RequestExecutorPractitioner(config.retryPolicyPractitioner()));
    }

}
