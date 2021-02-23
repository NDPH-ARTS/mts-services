package uk.ac.ox.ndph.mts.init_service.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

public interface TokenService {

    /**
     * Gets the token from the cloud provider
     * Initially we will use the default credential builder in Azure
     * @return An OAuth token in string format
     */
    String getToken();

    /**
     * Gets the token from the cloud provider
     * Initially we will use the default credential builder in Azure
     *
     * @return An OAuth token in string format
     */
    default String getIdentityId() {
        String token = getToken();
        String identityId = null;

        try {
            DecodedJWT jwt = JWT.decode(token);
            identityId = jwt.getId();
        } catch (JWTDecodeException jwtDecodeException) {
            //invalid token
        }

        return identityId;
    }

}
