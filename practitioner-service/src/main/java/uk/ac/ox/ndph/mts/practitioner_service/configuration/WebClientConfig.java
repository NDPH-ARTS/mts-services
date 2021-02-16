package uk.ac.ox.ndph.mts.practitioner_service.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
@Profile("!local")
@ConfigurationProperties(prefix = "http")
public class WebClientConfig {

    @Value("${http.connectTimeOutMs:1000}")
    private int connectTimeOutMs;

    @Value("${http.readTimeOutMs:10000}")
    private long readTimeOutMs;

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

    public WebClient webClient() {
        return webClientBuilder().build();
    }

    public int getConnectTimeOutMs() {
        return this.connectTimeOutMs;
    }

    public void setConnectTimeOutMs(final int connectTimeOutMs) {
        this.connectTimeOutMs = connectTimeOutMs;
    }

    public long getReadTimeOutMs() {
        return this.readTimeOutMs;
    }

    public void setReadTimeOutMs(final long readTimeOutMs) {
        this.readTimeOutMs = readTimeOutMs;
    }

}
