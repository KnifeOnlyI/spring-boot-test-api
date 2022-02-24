package fr.koi.testapi.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represent a user authenticator public model
 */
@Getter
@Setter
@Accessors(chain = true)
public class UserAuthenticator {
    /**
     * The login or email
     */
    private String loginOrEmail;

    /**
     * The password
     */
    private String password;

    /**
     * The remember-me flag
     */
    private Boolean rememberMe;
}
