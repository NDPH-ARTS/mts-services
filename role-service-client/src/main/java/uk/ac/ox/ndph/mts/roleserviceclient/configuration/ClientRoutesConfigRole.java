package uk.ac.ox.ndph.mts.roleserviceclient.configuration;

import org.springframework.stereotype.Component;
//Make this class a spring @Configuration class with @Value annotation
@Component
public class ClientRoutesConfigRole {
    static final String SERVICE_NAME = "role-service";
    static final String SERVICE_GET_ROLE = "/roles/{id}";
    static final String SERVICE_GET_PAGED = "/roles/";
    static final String SERVICE_ROLES_BY_IDS = "roles";
    static final String SERVICE_CREATE_ROLE =  "/roles";
    static final String SERVICE_UPDATE_PERMISSIONS = "/roles/{id}/permissions";
    public static String getServiceName() {
        return SERVICE_NAME;
    }
    public static String getServiceGetRole() {
        return SERVICE_GET_ROLE;
    }

    public static String getServiceGetPaged() {
        return SERVICE_GET_PAGED;
    }

    public static String getServiceRolesByIds() {
        return SERVICE_ROLES_BY_IDS;
    }

    public static  String getServiceCreateRole() {
        return SERVICE_CREATE_ROLE;
    }

    public static String getServiceUpdatePermissions() {
        return SERVICE_UPDATE_PERMISSIONS;
    }

}
