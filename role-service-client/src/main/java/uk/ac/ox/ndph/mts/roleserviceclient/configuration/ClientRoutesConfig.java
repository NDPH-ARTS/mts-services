package uk.ac.ox.ndph.mts.roleserviceclient.configuration;

import org.springframework.stereotype.Component;
//TODO: Make this class a spring @Configuration class with @Value annotation
@Component
public class ClientRoutesConfig {
    static final String SERVICE_NAME = "role-service";
    static final String SERVICE_EXISTS_ROUTE = "/roles/{id}";
    static final String SERVICE_GET_ROLE = "/roles/{id}";
    static final String SERVICE_GET_PAGED = "/roles/";
    static final String SERVICE_ROLES_BY_IDS = "roles";
    static final String SERVICE_CREATE_ROLE =  "/roles";
    static final String SERVICE_UPDATE_PERMISSIONS = "/roles/{id}/permissions";
    public String getServiceName() {
        return SERVICE_NAME;
    }

    public String getServiceExistsRoute() {
        return SERVICE_EXISTS_ROUTE;
    }

    public String getServiceGetRole() {
        return SERVICE_GET_ROLE;
    }

    public String getServiceGetPaged() {
        return SERVICE_GET_PAGED;
    }

    public String getServiceRolesByIds() {
        return SERVICE_ROLES_BY_IDS;
    }

    public String getServiceCreateRole() {
        return SERVICE_CREATE_ROLE;
    }

    public String getServiceUpdatePermissions() {
        return SERVICE_UPDATE_PERMISSIONS;
    }

}
