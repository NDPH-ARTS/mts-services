package uk.ac.ox.ndph.mts.roleserviceclient.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Configuration
@ConfigurationProperties(prefix = "http-client")
public class WebClientConfig {

    @Value("${http-client.connectTimeOutMs:3000}")
    private int connectTimeOutMs;

    @Value("${http-client.readTimeOutMs:30000}")
    private long readTimeOutMs;

    @Value("${http-client.maxRetryAttempts:9}")
    private int maxRetryAttempts;

    @Value("${http-client.initialRetryDurationMs:5000}")
    private long initialRetryDurationMs;

    @Value("${http-client.maxRetryDurationMs:30000}")
    private long maxRetryDurationMs;

    public ClientHttpConnector connectorWithConnectAndReadTimeOuts() {
        return new ReactorClientHttpConnector(HttpClient.create()
            .tcpConfiguration(tcpClient ->
                tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getConnectTimeOutMs())
                    .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(getReadTimeOutMs(), TimeUnit.MILLISECONDS)))));
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder().clientConnector(connectorWithConnectAndReadTimeOuts());
    }

    @Bean
    public Supplier<Retry> retryPolicy() {
        return () -> Retry.backoff(getMaxRetryAttempts(),
            Duration.ofMillis(getInitialRetryDurationMs()))
            .maxBackoff(Duration.ofMillis(getMaxRetryDurationMs()));
    }

    public int getConnectTimeOutMs() {
        return connectTimeOutMs;
    }

    public void setConnectTimeOutMs(int connectTimeOutMs) {
        this.connectTimeOutMs = connectTimeOutMs;
    }

    public long getReadTimeOutMs() {
        return readTimeOutMs;
    }

    public void setReadTimeOutMs(long readTimeOutMs) {
        this.readTimeOutMs = readTimeOutMs;
    }

    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public long getInitialRetryDurationMs() {
        return initialRetryDurationMs;
    }

    public void setInitialRetryDurationMs(long initialRetryDurationMs) {
        this.initialRetryDurationMs = initialRetryDurationMs;
    }

    public long getMaxRetryDurationMs() {
        return maxRetryDurationMs;
    }

    public void setMaxRetryDurationMs(long maxRetryDurationMs) {
        this.maxRetryDurationMs = maxRetryDurationMs;
    }

}
