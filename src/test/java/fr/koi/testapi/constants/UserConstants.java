package fr.koi.testapi.constants;

import fr.koi.testapi.domain.UserEntity;
import fr.koi.testapi.util.DateUtil;

/**
 * Contains all constants about users testing
 */
public final class UserConstants {
    public static final class Users {
        public static final UserEntity ACTIVATED = new UserEntity()
            .setId(1L)
            .setEmail("activated@user.com")
            .setLogin("activated")
            .setPassword("P@ssword")
            .setCreatedAt(DateUtil.fromDatabaseString("2021-12-31 23:59:59"))
            .setActivated(true);

        public static final UserEntity DESACTIVATED = new UserEntity()
            .setId(2L)
            .setEmail("desactivated@user.com")
            .setLogin("desactivated")
            .setPassword("P@ssword")
            .setCreatedAt(DateUtil.fromDatabaseString("2022-01-01 00:00:00"))
            .setActivated(false);
    }
}
