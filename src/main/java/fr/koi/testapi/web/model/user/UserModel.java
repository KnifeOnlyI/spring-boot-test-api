package fr.koi.testapi.web.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Represent a user public model
 */
@Getter
@Setter
@Accessors(chain = true)
public class UserModel {
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

    /**
     * The creation date
     */
    private String createdAt;
}
