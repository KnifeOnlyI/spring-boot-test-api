package fr.koi.testapi.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represent a JWT token public model
 */
@Getter
@Setter
@Accessors(chain = true)
public class JwtTokenModel {
    /**
     * The token value
     */
    private String token;
}
