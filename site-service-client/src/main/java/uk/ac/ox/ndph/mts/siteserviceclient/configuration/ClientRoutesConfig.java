package uk.ac.ox.ndph.mts.siteserviceclient.configuration;

import org.springframework.stereotype.Component;
//Make this class a spring @Configuration class with @Value annotation
@Component
public class ClientRoutesConfig {
    static final String SERVICE_NAME = "site-service";
    static final String SERVICE_GET_SITE = "/sites/{id}";
    static final String SERVICE_CREATE_SITE =  "/sites";

    public static String getServiceGetAllSites() {
        return SERVICE_GET_ALL_SITES;
    }

    static final String SERVICE_GET_ALL_SITES =  "/sites";
    public String getServiceName() {
        return SERVICE_NAME;
    }

    public String getServiceGetSite() {
        return SERVICE_GET_SITE;
    }

    public String getServiceCreateSite() {
        return SERVICE_CREATE_SITE;
    }

}
