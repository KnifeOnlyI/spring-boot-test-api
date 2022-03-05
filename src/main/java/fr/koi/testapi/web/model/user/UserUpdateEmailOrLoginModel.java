package fr.koi.testapi.web.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represent a public model to update user's email and/or login
 */
@Getter
@Setter
@Accessors(chain = true)
public class UserUpdateEmailOrLoginModel {
    /**
     * The ID
     */
    private Long id;

    /**
     * The email
     */
    private String email;

    /**
     * The login
     */
    private String login;
}
