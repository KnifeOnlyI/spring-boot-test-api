package fr.koi.testapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Represent a JWT token DTOL
 */
@Accessors(chain = true)
@Getter
@Setter
public class JwtTokenDTO {
    /**
     * The user id
     */
    private Long userId;

    /**
     * The user agent
     */
    private String userAgent;

    /**
     * The IP of client
     */
    private String clientIp;

    /**
     * The expiration date
     */
    private Date expirationDate;
}
