package uk.ac.ox.ndph.mts.init_service.config;

public interface TokenService {

    /**
     * Gets the token from the cloud provider
     * Initially we will use the default credential builder in Azure
     * @return An OAuth token in string format
     */
    String getToken();

}
