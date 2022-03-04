package fr.koi.testapi.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represent a user register public model
 */
@Getter
@Setter
@Accessors(chain = true)
public class UserRegisterModel {
    /**
     * The email
     */
    private String email;

    /**
     * The login
     */
    private String login;

    /**
     * The password
     */
    private String password;
}
