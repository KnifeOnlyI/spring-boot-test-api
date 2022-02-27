package fr.koi.testapi.constants;

import fr.koi.testapi.domain.UserEntity;
import fr.koi.testapi.model.user.UserRegister;
import fr.koi.testapi.util.DateUtil;

/**
 * Contains all constants about users testing
 */
public final class UserConstants {
    /**
     * Contains all constants about users
     */
    public static final class Users {
        /**
         * An activated user
         */
        public static final UserEntity ACTIVATED = new UserEntity()
            .setId(1L)
            .setEmail("activated@user.com")
            .setLogin("activated")
            .setPassword("P@ssword")
            .setCreatedAt(DateUtil.fromDatabaseString("2021-12-31 23:59:59"))
            .setActivated(true);

        /**
         * A desactivated user
         */
        public static final UserEntity DESACTIVATED = new UserEntity()
            .setId(2L)
            .setEmail("desactivated@user.com")
            .setLogin("desactivated")
            .setPassword("P@ssword")
            .setCreatedAt(DateUtil.fromDatabaseString("2022-01-01 00:00:00"))
            .setActivated(false);

        /**
         * A valid register public model of non existing user
         */
        public static final UserRegister NEW_VALID_USER = new UserRegister()
            .setEmail("new_valid@user.com")
            .setLogin("new_valid")
            .setPassword("1P@ssword");

        /**
         * Get a copy of specified user register public model
         *
         * @param userRegister The user register to copy
         *
         * @return The corresponding copy
         */
        public static UserRegister getCopy(UserRegister userRegister) {
            return new UserRegister()
                .setEmail(userRegister.getEmail())
                .setLogin(userRegister.getLogin())
                .setPassword(userRegister.getPassword());
        }
    }
}
