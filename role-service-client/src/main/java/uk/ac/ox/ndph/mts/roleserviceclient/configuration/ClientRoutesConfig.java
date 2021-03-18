package uk.ac.ox.ndph.mts.roleserviceclient.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties("role")
@PropertySource(value = "classpath:client-config.yml", factory = YamlPropertySourceFactory.class)
public class ClientRoutesConfig {

    @Value("${role.service.name}")
    public String serviceName;

    @Value("${role.service.endpoint.exists}")
    public String serviceExistsRoute;

    @Value("${role.service.endpoint.role}")
    public String serviceGetRole;

    @Value("${role.service.endpoint.paged}")
    public String serviceGetPaged;

    @Value("${role.service.endpoint.roles.by.ids}")
    public String serviceRolesByIds;

    @Value("${role.service.endpoint.roles.create}")
    public String serviceCreateRole;

    @Value("${role.service.endpoint.update.permissions}")
    public String serviceUpdatePermissions;

}
