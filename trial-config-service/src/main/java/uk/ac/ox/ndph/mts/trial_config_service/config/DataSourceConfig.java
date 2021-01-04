package uk.ac.ox.ndph.mts.trial_config_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig implements DataSourceConfigService {
    @Value("${jdbc.driver}")
    private String driverClass;
    @Value("${jdbc.url}")
    private String url;

    @Bean
    public DataSource getDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(getDriverClass())
                .url(url)
                .build();
    }

    @Override
    public String getDriverClass() {
        return driverClass;
    }

    @Override
    public String getUrl() {
        return url;
    }
}
