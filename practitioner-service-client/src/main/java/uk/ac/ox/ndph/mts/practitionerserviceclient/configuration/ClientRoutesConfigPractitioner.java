package uk.ac.ox.ndph.mts.practitionerserviceclient.configuration;

import org.springframework.stereotype.Component;
//Make this class a spring @Configuration class with @Value annotation
@Component
public final class ClientRoutesConfigPractitioner {
    static final String SERVICE_NAME = "practitioner-service";
    static final String SERVICE_GET_ROLE_ASSIGNMENT = "/practitioner/roles";
    static final String SERVICE_CREATE_PRACTITIONER = "/practitioner";
    static final String SERVICE_ASSIGN_ROLE = "/practitioner/{id}/roles";
    static final String SERVICE_LINK_USER_ACCOUNT = "/practitioner/{id}/link";

    private ClientRoutesConfigPractitioner() { }

    public static String getServiceGetRoleAssignment() {
        return SERVICE_GET_ROLE_ASSIGNMENT;
    }

    public static String getServiceCreatePractitioner() {
        return SERVICE_CREATE_PRACTITIONER;
    }

    public static String getServiceAssignRole() {
        return SERVICE_ASSIGN_ROLE;
    }

    public static String getServiceLinkUserAccount() {
        return SERVICE_LINK_USER_ACCOUNT;
    }

}
