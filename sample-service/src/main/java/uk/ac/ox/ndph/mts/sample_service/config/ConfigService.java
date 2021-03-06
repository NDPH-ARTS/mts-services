package uk.ac.ox.ndph.mts.sample_service.config;

/**
 * An interface to allow access to secrets.
 */
public interface ConfigService {

    /**
     * Gets the secret.
     * @return The secret as a string.
     */
    String getSecret();
}
