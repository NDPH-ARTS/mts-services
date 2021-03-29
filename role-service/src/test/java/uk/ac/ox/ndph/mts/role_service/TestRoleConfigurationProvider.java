package uk.ac.ox.ndph.mts.role_service;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import uk.ac.ox.ndph.mts.role_service.config.DataSourceConfig;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Profile("test-all-required")
@Primary
@Configuration
public class TestRoleConfigurationProvider {

    @Bean
    @Primary
    public DataSourceConfig dataSourceConfig() {
        var mock = mock(DataSourceConfig.class);
        when(mock.getDataSource()).thenReturn(DataSourceBuilder.create().driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver").url("jdbc:sqlserver://localhost:1466;databaseName=master;user=sa;password=SomePasswordGoesHere123abc").build());
        return mock;
    }
}
