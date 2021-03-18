package uk.ac.ox.ndph.mts.roleserviceclient.configuration;

import org.springframework.stereotype.Component;
//TODO: Make this class a spring @Configuration class with @Value annotation
@Component
public class ClientRoutesConfig {

    public String getServiceName() {
        return "role-service";
    }

    public String getServiceExistsRoute() {
        return "/roles/{id}";
    }

    public String getServiceGetRole() {
        return "/roles/{id}";
    }

    public String getServiceGetPaged() {
        return "/roles/";
    }

    public String getServiceRolesByIds() {
        return "roles";
    }

    public String getServiceCreateRole() {
        return "/roles";
    }

    public String getServiceUpdatePermissions() {
        return "/roles/{id}/permissions";
    }

}
