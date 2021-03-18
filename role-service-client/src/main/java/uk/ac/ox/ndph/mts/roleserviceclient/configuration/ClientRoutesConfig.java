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
    private String serviceName;

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceExistsRoute() {
        return serviceExistsRoute;
    }

    public String getServiceGetRole() {
        return serviceGetRole;
    }

    public String getServiceGetPaged() {
        return serviceGetPaged;
    }

    public String getServiceRolesByIds() {
        return serviceRolesByIds;
    }

    public String getServiceCreateRole() {
        return serviceCreateRole;
    }

    public String getServiceUpdatePermissions() {
        return serviceUpdatePermissions;
    }

    @Value("${role.service.endpoint.exists}")
    private String serviceExistsRoute;

    @Value("${role.service.endpoint.role}")
    private String serviceGetRole;

    @Value("${role.service.endpoint.paged}")
    private String serviceGetPaged;

    @Value("${role.service.endpoint.roles.by.ids}")
    private String serviceRolesByIds;

    @Value("${role.service.endpoint.roles.create}")
    private String serviceCreateRole;

    @Value("${role.service.endpoint.update.permissions}")
    private String serviceUpdatePermissions;

}
