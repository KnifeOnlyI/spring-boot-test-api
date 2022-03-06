package fr.koi.testapi.constants;

import fr.koi.testapi.domain.UserEntity;
import fr.koi.testapi.utils.DateUtil;
import fr.koi.testapi.web.model.user.UserRegisterModel;
import fr.koi.testapi.web.model.user.UserUpdateEmailLoginModel;

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
        public static final UserRegisterModel NEW_VALID_USER = new UserRegisterModel()
            .setEmail("new_valid@user.com")
            .setLogin("new_valid")
            .setPassword("1P@ssword");

        /**
         * A valid update login or email public model of non existing user
         */
        public static final UserUpdateEmailLoginModel UPDATED_ACTIVATED_USER = new UserUpdateEmailLoginModel()
            .setId(ACTIVATED.getId())
            .setEmail("update_activated@user.com")
            .setLogin("update_activated");

        /**
         * Get a copy of specified model
         *
         * @param model The model to copy
         *
         * @return The corresponding copy
         */
        public static UserRegisterModel getCopy(UserRegisterModel model) {
            return new UserRegisterModel()
                .setEmail(model.getEmail())
                .setLogin(model.getLogin())
                .setPassword(model.getPassword());
        }

        /**
         * Get a copy of specified model
         *
         * @param model The model to copy
         *
         * @return The corresponding copy
         */
        public static UserUpdateEmailLoginModel getCopy(UserUpdateEmailLoginModel model) {
            return new UserUpdateEmailLoginModel()
                .setId(model.getId())
                .setEmail(model.getEmail())
                .setLogin(model.getLogin());
        }
    }
}
