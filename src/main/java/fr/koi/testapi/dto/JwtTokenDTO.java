package fr.koi.testapi.dto;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Represent a JWT token
 */
@Accessors(chain = true)
@Getter
@Setter
public class JwtTokenDTO {
    /**
     * The algorithm
     */
    private String algorithm;

    /**
     * The type
     */
    private String type;

    /**
     * The username
     */
    private String name;

    /**
     * The user agent
     */
    private String userAgent;

    /**
     * The IP
     */
    private String ip;

    /**
     * Creation date
     */
    private Date creationDate;

    /**
     * The expiration date
     */
    private Date expirationDate;

    /**
     * Create a new JWT token from the specified jwt data
     *
     * @param jwt The JWT data to use
     */
    public JwtTokenDTO(DecodedJWT jwt) {
        this.algorithm = jwt.getHeaderClaim("alg").asString();
        this.type = jwt.getHeaderClaim("typ").asString();
        this.name = jwt.getClaim("sub").asString();
        this.userAgent = jwt.getClaim("user_agent").asString();
        this.ip = jwt.getClaim("ip").asString();
        this.creationDate = jwt.getClaim("iat").asDate();
        this.expirationDate = jwt.getClaim("exp").asDate();
    }
}
