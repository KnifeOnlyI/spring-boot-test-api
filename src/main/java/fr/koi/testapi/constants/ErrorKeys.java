package fr.koi.testapi.constants;

/**
 * Contains all errors keys
 */
public final class ErrorKeys {
    private ErrorKeys() {
    }

    /**
     * Contains all error keys about user
     */
    public static final class User {
        /**
         * Error when invalid credentials are detected
         */
        public static final String LOGIN_INVALID_CREDENTIALS = "error.login.invalidCredentials";

        /**
         * Error when trying to login with desactivated user
         */
        public static final String LOGIN_DESACTIVATED = "error.login.desactivatedUser";

        /**
         * Error when trying to login without User-Agent header
         */
        public static final String LOGIN_HEADER_USER_AGENT_NULL = "error.login.header.user_agent.null";

        /**
         * Error when trying to login without X-Forwarded-For header
         */
        public static final String LOGIN_HEADER_X_FORWARDED_FOR_NULL = "error.login.header.x_forwarded_for.null";

        /**
         * Error when trying to logout without token
         */
        public static final String LOGOUT_TOKEN_NULL = "error.logout.token.null";

        /**
         * Error when trying to logout with a non existant token
         */
        public static final String LOGOUT_TOKEN_NOT_EXISTS = "error.logout.token.not_exists";

        /**
         * Error when trying to register without email
         */
        public static final String REGISTER_EMAIL_NULL = "error.register.email.null";

        /**
         * Error when trying to register without login
         */
        public static final String REGISTER_LOGIN_NULL = "error.register.login.null";

        /**
         * Error when trying to register without password
         */
        public static final String REGISTER_PASSWORD_NULL = "error.register.password.null";

        /**
         * Error when trying to register with invalid formatted email
         */
        public static final String REGISTER_EMAIL_INVALID = "error.register.email.invalid";

        /**
         * Error when trying to register with invalid formatted login
         */
        public static final String REGISTER_LOGIN_INVALID = "error.register.login.invalid";

        /**
         * Error when trying to register with invalid formatted password
         */
        public static final String REGISTER_PASSWORD_INVALID = "error.register.password.invalid";

        /**
         * Error when trying to register with already existing email
         */
        public static final String REGISTER_EMAIL_ALREADY_EXISTS = "error.register.email.alreadyExists";

        /**
         * Error when trying to register with already existing login
         */
        public static final String REGISTER_LOGIN_ALREADY_EXISTS = "error.register.login.alreadyExists";

        private User() {
        }
    }
}
