package fr.koi.testapi.resource.user;

import fr.koi.testapi.constants.ErrorKeys;
import fr.koi.testapi.constants.UserConstants;
import fr.koi.testapi.domain.TokenEntity;
import fr.koi.testapi.domain.UserEntity;
import fr.koi.testapi.dto.JwtTokenDTO;
import fr.koi.testapi.model.user.JwtTokenModel;
import fr.koi.testapi.model.user.UserAuthenticatorModel;
import fr.koi.testapi.repository.TokenRepository;
import fr.koi.testapi.repository.UserRepository;
import fr.koi.testapi.services.JwtService;
import fr.koi.testapi.util.HttpResponseAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * Test class to test the web resource to manage users
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserResourceTest {
    /**
     * The default user agent
     */
    private static final String DEFAULT_USER_AGENT = "user-agent";

    /**
     * The default client IP
     */
    private static final String DEFAULT_CLIENT_IP = "127.0.0.1";

    /**
     * The web resource to manage users
     */
    @Autowired
    private UserResource userResource;

    /**
     * The repository to manage tokens in database
     */
    @Autowired
    private TokenRepository tokenRepository;

    /**
     * The repository to manage users in database
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The service to manage JWT tokens
     */
    @Autowired
    private JwtService jwtService;

    /**
     * Provide a list of invalid emails
     *
     * @return List of invalid emails
     */
    private static Stream<String> invalidEmailProvider() {
        return Stream.of(
            "hello",                            // email need at least one @
            "hello@ "                           // domain cant end with space (whitespace)
        );
    }

    /**
     * Provide a list of invalid login
     *
     * @return List of invalid login
     */
    private static Stream<String> invalidLoginProvider() {
        return Stream.of(
            "abc",                      // invalid length 3, length must between 5-20
            "01234567890123456789a",    // invalid length 21, length must between 5-20
            "_javaregex_",              // invalid start and last character
            ".javaregex.",              // invalid start and last character
            "-javaregex-",              // invalid start and last character
            "javaregex#$%@123",         // invalid symbols, support dot, hyphen and underscore
            "java..regex",              // dot cant appear consecutively
            "java--regex",              // hyphen can't appear consecutively
            "java__regex",              // underscore can't appear consecutively
            "java._regex",              // dot and underscore can't appear consecutively
            "java.-regex"               // dot and hyphen can't appear consecutively
        );
    }

    /**
     * Provide a list of invalid passwords
     *
     * @return List of invalid passwords
     */
    private static Stream<String> invalidPasswordProvider() {
        return Stream.of(
            "12345678",                 // invalid, only digit
            "abcdefgh",                 // invalid, only lowercase
            "ABCDEFGH",                 // invalid, only uppercase
            "abc123$$$",                // invalid, at least one uppercase
            "ABC123$$$",                // invalid, at least one lowercase
            "ABC$$$$$$",                // invalid, at least one digit
            "java REGEX 123",           // invalid, at least one special character
            "java REGEX 123 %",         // invalid, % is not in the special character group []
            "________",                 // invalid
            "--------"                  // invalid
        );
    }

    /**
     * Test a valid login with email and remember me
     */
    @Test
    void testValidLoginWithEmailAndRememberMe() {
        UserAuthenticatorModel userAuthenticator = new UserAuthenticatorModel()
            .setLoginOrEmail(UserConstants.Users.ACTIVATED.getEmail())
            .setPassword(UserConstants.Users.ACTIVATED.getPassword())
            .setRememberMe(true);

        // Check if response is valid

        JwtTokenModel response = new HttpResponseAssert<>(this.userResource.login(
            DEFAULT_USER_AGENT,
            DEFAULT_CLIENT_IP,
            userAuthenticator
        )).assertHttpStatus(HttpStatus.OK)
            .assertNbHeaders(0)
            .getNotNullBody();

        Assertions.assertNotNull(response.getToken());

        // Check if the received token contains correct information

        JwtTokenDTO tokenDTO = this.jwtService.verify(response.getToken());

        Assertions.assertEquals(UserConstants.Users.ACTIVATED.getId(), tokenDTO.getUserId());
        Assertions.assertEquals(DEFAULT_USER_AGENT, tokenDTO.getUserAgent());
        Assertions.assertEquals(DEFAULT_CLIENT_IP, tokenDTO.getClientIp());
        Assertions.assertNull(tokenDTO.getExpirationDate());

        // Check if token in database correspond to the token received from REST API

        List<TokenEntity> tokens = this.tokenRepository.findAll();

        Assertions.assertEquals(1, tokens.size());

        TokenEntity createdToken = tokens.get(0);

        Assertions.assertEquals(UserConstants.Users.ACTIVATED.getId(), createdToken.getId());
        Assertions.assertEquals(response.getToken(), createdToken.getValue());
        Assertions.assertNotNull(createdToken.getCreatedAt());
        Assertions.assertFalse(createdToken.getDeleted());
    }

    /**
     * Test a valid login with login and remember me
     */
    @Test
    void testValidLoginWithLoginAndRememberMe() {
        UserAuthenticatorModel userAuthenticator = new UserAuthenticatorModel()
            .setLoginOrEmail(UserConstants.Users.ACTIVATED.getLogin())
            .setPassword(UserConstants.Users.ACTIVATED.getPassword())
            .setRememberMe(true);

        // Check if response is valid

        JwtTokenModel response = new HttpResponseAssert<>(this.userResource.login(
            DEFAULT_USER_AGENT,
            DEFAULT_CLIENT_IP,
            userAuthenticator
        )).assertHttpStatus(HttpStatus.OK)
            .assertNbHeaders(0)
            .getNotNullBody();

        Assertions.assertNotNull(response.getToken());

        // Check if the received token contains correct information

        JwtTokenDTO tokenDTO = this.jwtService.verify(response.getToken());

        Assertions.assertEquals(UserConstants.Users.ACTIVATED.getId(), tokenDTO.getUserId());
        Assertions.assertEquals(DEFAULT_USER_AGENT, tokenDTO.getUserAgent());
        Assertions.assertEquals(DEFAULT_CLIENT_IP, tokenDTO.getClientIp());
        Assertions.assertNull(tokenDTO.getExpirationDate());

        // Check if token in database correspond to the token received from REST API

        List<TokenEntity> tokens = this.tokenRepository.findAll();

        Assertions.assertEquals(1, tokens.size());

        TokenEntity createdToken = tokens.get(0);

        Assertions.assertEquals(UserConstants.Users.ACTIVATED.getId(), createdToken.getId());
        Assertions.assertEquals(response.getToken(), createdToken.getValue());
        Assertions.assertNotNull(createdToken.getCreatedAt());
        Assertions.assertFalse(createdToken.getDeleted());
    }

    /**
     * Test a valid login with email without remember me
     */
    @Test
    void testValidLoginWithEmailWithoutRememberMe() {
        UserAuthenticatorModel userAuthenticator = new UserAuthenticatorModel()
            .setLoginOrEmail(UserConstants.Users.ACTIVATED.getEmail())
            .setPassword(UserConstants.Users.ACTIVATED.getPassword())
            .setRememberMe(false);

        // Check if response is valid

        JwtTokenModel response = new HttpResponseAssert<>(this.userResource.login(
            DEFAULT_USER_AGENT,
            DEFAULT_CLIENT_IP,
            userAuthenticator
        )).assertHttpStatus(HttpStatus.OK)
            .assertNbHeaders(0)
            .getNotNullBody();

        Assertions.assertNotNull(response.getToken());

        // Check if the received token contains correct information

        JwtTokenDTO tokenDTO = this.jwtService.verify(response.getToken());

        Assertions.assertEquals(UserConstants.Users.ACTIVATED.getId(), tokenDTO.getUserId());
        Assertions.assertEquals(DEFAULT_USER_AGENT, tokenDTO.getUserAgent());
        Assertions.assertEquals(DEFAULT_CLIENT_IP, tokenDTO.getClientIp());
        Assertions.assertNotNull(tokenDTO.getExpirationDate());
        Assertions.assertTrue(new Date().before(tokenDTO.getExpirationDate()));

        // Check if token in database correspond to the token received from REST API

        List<TokenEntity> tokens = this.tokenRepository.findAll();

        Assertions.assertEquals(1, tokens.size());

        TokenEntity createdToken = tokens.get(0);

        Assertions.assertEquals(UserConstants.Users.ACTIVATED.getId(), createdToken.getId());
        Assertions.assertEquals(response.getToken(), createdToken.getValue());
        Assertions.assertNotNull(createdToken.getCreatedAt());
        Assertions.assertFalse(createdToken.getDeleted());
    }

    /**
     * Test a valid login with login without remember me
     */
    @Test
    void testValidLoginWithLoginWithoutRememberMe() {
        UserAuthenticatorModel userAuthenticator = new UserAuthenticatorModel()
            .setLoginOrEmail(UserConstants.Users.ACTIVATED.getLogin())
            .setPassword(UserConstants.Users.ACTIVATED.getPassword())
            .setRememberMe(false);

        // Check if response is valid

        JwtTokenModel response = new HttpResponseAssert<>(this.userResource.login(
            DEFAULT_USER_AGENT,
            DEFAULT_CLIENT_IP,
            userAuthenticator
        )).assertHttpStatus(HttpStatus.OK)
            .assertNbHeaders(0)
            .getNotNullBody();

        Assertions.assertNotNull(response.getToken());

        // Check if the received token contains correct information

        JwtTokenDTO tokenDTO = this.jwtService.verify(response.getToken());

        Assertions.assertEquals(UserConstants.Users.ACTIVATED.getId(), tokenDTO.getUserId());
        Assertions.assertEquals(DEFAULT_USER_AGENT, tokenDTO.getUserAgent());
        Assertions.assertEquals(DEFAULT_CLIENT_IP, tokenDTO.getClientIp());
        Assertions.assertNotNull(tokenDTO.getExpirationDate());
        Assertions.assertTrue(new Date().before(tokenDTO.getExpirationDate()));

        // Check if token in database correspond to the token received from REST API

        List<TokenEntity> tokens = this.tokenRepository.findAll();

        Assertions.assertEquals(1, tokens.size());

        TokenEntity createdToken = tokens.get(0);

        Assertions.assertEquals(UserConstants.Users.ACTIVATED.getId(), createdToken.getId());
        Assertions.assertEquals(response.getToken(), createdToken.getValue());
        Assertions.assertNotNull(createdToken.getCreatedAt());
        Assertions.assertFalse(createdToken.getDeleted());
    }

    /**
     * Test an invalid login because invalid email / password without remember me
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testInvalidLoginWithInvalidEmailAndPasswordBecauseInvalidCredentials() {
        UserAuthenticatorModel userAuthenticator = new UserAuthenticatorModel()
            .setLoginOrEmail(null)
            .setPassword(null)
            .setRememberMe(false);

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.login(
            DEFAULT_USER_AGENT, DEFAULT_CLIENT_IP, userAuthenticator
        ), HttpStatus.UNAUTHORIZED, ErrorKeys.User.LOGIN_INVALID_CREDENTIALS);

        Assertions.assertEquals(0, this.tokenRepository.findAll().size());
    }

    /**
     * Test an invalid login because invalid login / password without remember me
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testInvalidLoginWithInvalidCredentialsLoginPasswordBecauseInvalidCredentials() {
        UserAuthenticatorModel userAuthenticator = new UserAuthenticatorModel()
            .setLoginOrEmail(null)
            .setPassword(null)
            .setRememberMe(false);

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.login(
            DEFAULT_USER_AGENT, DEFAULT_CLIENT_IP, userAuthenticator
        ), HttpStatus.UNAUTHORIZED, ErrorKeys.User.LOGIN_INVALID_CREDENTIALS);

        Assertions.assertEquals(0, this.tokenRepository.findAll().size());
    }

    /**
     * Test an invalid login with valid desactivated email and invalid password without remember me
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testInvalidLoginWithValidEmailAndInvalidPasswordAndDesactivated() {
        UserAuthenticatorModel userAuthenticator = new UserAuthenticatorModel()
            .setLoginOrEmail(UserConstants.Users.DESACTIVATED.getEmail())
            .setPassword(null)
            .setRememberMe(false);

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.login(
            DEFAULT_USER_AGENT, DEFAULT_CLIENT_IP, userAuthenticator
        ), HttpStatus.UNAUTHORIZED, ErrorKeys.User.LOGIN_INVALID_CREDENTIALS);

        Assertions.assertEquals(0, this.tokenRepository.findAll().size());
    }

    /**
     * Test an invalid login with valid desactivated login and invalid password without remember me
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testInvalidLoginWithLoginAndInvalidPasswordAndDesactivated() {
        UserAuthenticatorModel userAuthenticator = new UserAuthenticatorModel()
            .setLoginOrEmail(UserConstants.Users.DESACTIVATED.getLogin())
            .setPassword(null)
            .setRememberMe(false);

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.login(
            DEFAULT_USER_AGENT, DEFAULT_CLIENT_IP, userAuthenticator
        ), HttpStatus.UNAUTHORIZED, ErrorKeys.User.LOGIN_INVALID_CREDENTIALS);

        Assertions.assertEquals(0, this.tokenRepository.findAll().size());
    }

    /**
     * Test an invalid login with valid desactivated email and password without remember me
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testInvalidLoginWithValidEmailAndPasswordBecauseDesactivated() {
        UserAuthenticatorModel userAuthenticator = new UserAuthenticatorModel()
            .setLoginOrEmail(UserConstants.Users.DESACTIVATED.getEmail())
            .setPassword(UserConstants.Users.DESACTIVATED.getPassword())
            .setRememberMe(false);

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.login(
            DEFAULT_USER_AGENT, DEFAULT_CLIENT_IP, userAuthenticator
        ), HttpStatus.UNAUTHORIZED, ErrorKeys.User.LOGIN_DESACTIVATED);

        Assertions.assertEquals(0, this.tokenRepository.findAll().size());
    }

    /**
     * Test an invalid login with valid desactivated login and password without remember me
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testInvalidLoginWithValidLoginAndPasswordBecauseDesactivated() {
        UserAuthenticatorModel userAuthenticator = new UserAuthenticatorModel()
            .setLoginOrEmail(UserConstants.Users.DESACTIVATED.getLogin())
            .setPassword(UserConstants.Users.DESACTIVATED.getPassword())
            .setRememberMe(false);

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.login(
            DEFAULT_USER_AGENT, DEFAULT_CLIENT_IP, userAuthenticator
        ), HttpStatus.UNAUTHORIZED, ErrorKeys.User.LOGIN_DESACTIVATED);

        Assertions.assertEquals(0, this.tokenRepository.findAll().size());
    }

    /**
     * Test an invalid login because missing headers :
     * - User-Agent
     * - X-Forwarded-For
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testInvalidLoginBecauseMissingUserAgentAndXForwardedForHeaders() {
        UserAuthenticatorModel userAuthenticator = new UserAuthenticatorModel()
            .setLoginOrEmail(UserConstants.Users.ACTIVATED.getEmail())
            .setPassword(UserConstants.Users.ACTIVATED.getPassword())
            .setRememberMe(true);

        HttpResponseAssert.AssertRestException(
            () -> this.userResource.login(null, null, userAuthenticator),
            HttpStatus.BAD_REQUEST,
            ErrorKeys.User.LOGIN_HEADER_USER_AGENT_NULL
        );
    }

    /**
     * Test an invalid login because missing header : User-Agent
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testInvalidLoginBecauseMissingUserAgentHeader() {
        UserAuthenticatorModel userAuthenticator = new UserAuthenticatorModel()
            .setLoginOrEmail(UserConstants.Users.ACTIVATED.getEmail())
            .setPassword(UserConstants.Users.ACTIVATED.getPassword())
            .setRememberMe(true);

        HttpResponseAssert.AssertRestException(
            () -> this.userResource.login(null, DEFAULT_CLIENT_IP, userAuthenticator),
            HttpStatus.BAD_REQUEST,
            ErrorKeys.User.LOGIN_HEADER_USER_AGENT_NULL
        );
    }

    /**
     * Test an invalid login because missing header : X-Forwarded-For
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testInvalidLoginBecauseMissingXForwardedForHeader() {
        UserAuthenticatorModel userAuthenticator = new UserAuthenticatorModel()
            .setLoginOrEmail(UserConstants.Users.ACTIVATED.getEmail())
            .setPassword(UserConstants.Users.ACTIVATED.getPassword())
            .setRememberMe(true);

        HttpResponseAssert.AssertRestException(
            () -> this.userResource.login(DEFAULT_USER_AGENT, null, userAuthenticator),
            HttpStatus.BAD_REQUEST,
            ErrorKeys.User.LOGIN_HEADER_X_FORWARDED_FOR_NULL
        );
    }

    /**
     * Test a valid register
     */
    @Test
    @Transactional
    void testValidRegister() {
        int nbInDbBeforeTest = this.userRepository.findAll().size();

        // Check if response is valid

        new HttpResponseAssert<>(
            this.userResource.register(UserConstants.Users.getCopy(UserConstants.Users.NEW_VALID_USER))
        )
            .assertHttpStatus(HttpStatus.OK)
            .assertNbHeaders(0)
            .assertNullBody();

        int nbInDbAfterTest = this.userRepository.findAll().size();

        // Check if the user was created in the database

        Assertions.assertEquals(nbInDbBeforeTest + 1, nbInDbAfterTest);

        UserEntity user = this.userRepository.findByLoginOrEmail(
            UserConstants.Users.NEW_VALID_USER.getLogin(),
            UserConstants.Users.NEW_VALID_USER.getLogin()
        ).orElse(null);

        Assertions.assertNotNull(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(UserConstants.Users.NEW_VALID_USER.getEmail(), user.getEmail());
        Assertions.assertEquals(UserConstants.Users.NEW_VALID_USER.getLogin(), user.getLogin());
        Assertions.assertEquals(UserConstants.Users.NEW_VALID_USER.getPassword(), user.getPassword());
        Assertions.assertNotNull(user.getCreatedAt());
        Assertions.assertFalse(user.getActivated());
        Assertions.assertNull(user.getTokens());
    }

    /**
     * Test an invalid register because no email
     */
    @Test
    void testInvalidRegisterBecauseNoEmail() {
        int nbInDbBeforeTest = this.userRepository.findAll().size();

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.register(
            UserConstants.Users.getCopy(UserConstants.Users.NEW_VALID_USER).setEmail(null)
        ), HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_EMAIL_NULL);

        // Check the user was not created in the database

        int nbInDbAfterTest = this.userRepository.findAll().size();

        Assertions.assertEquals(nbInDbBeforeTest, nbInDbAfterTest);
    }

    /**
     * Test an invalid register because empty email
     */
    @Test
    void testInvalidRegisterBecauseEmptyEmail() {
        int nbInDbBeforeTest = this.userRepository.findAll().size();

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.register(
            UserConstants.Users.getCopy(UserConstants.Users.NEW_VALID_USER).setEmail("")
        ), HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_EMAIL_NULL);

        // Check the user was not created in the database

        int nbInDbAfterTest = this.userRepository.findAll().size();

        Assertions.assertEquals(nbInDbBeforeTest, nbInDbAfterTest);
    }

    /**
     * Test an invalid register because invalid email
     */
    @ParameterizedTest(name = "#{index} - Run test with email = {0}")
    @MethodSource("invalidEmailProvider")
    void testInvalidRegisterBecauseInvalidEmail(String email) {
        int nbInDbBeforeTest = this.userRepository.findAll().size();

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.register(
            UserConstants.Users.getCopy(UserConstants.Users.NEW_VALID_USER).setEmail(email)
        ), HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_EMAIL_INVALID);

        // Check the user was not created in the database

        int nbInDbAfterTest = this.userRepository.findAll().size();

        Assertions.assertEquals(nbInDbBeforeTest, nbInDbAfterTest);
    }

    /**
     * Test an invalid register because email already exists
     */
    @Test
    void testInvalidRegisterBecauseAlreadyExistsEmail() {
        int nbInDbBeforeTest = this.userRepository.findAll().size();

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.register(
            UserConstants.Users.getCopy(UserConstants.Users.NEW_VALID_USER).setEmail(UserConstants.Users.ACTIVATED.getEmail())
        ), HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_EMAIL_ALREADY_EXISTS);

        // Check the user was not created in the database

        int nbInDbAfterTest = this.userRepository.findAll().size();

        Assertions.assertEquals(nbInDbBeforeTest, nbInDbAfterTest);
    }

    /**
     * Test an invalid register because no login
     */
    @Test
    void testInvalidRegisterBecauseNoLogin() {
        int nbInDbBeforeTest = this.userRepository.findAll().size();

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.register(
            UserConstants.Users.getCopy(UserConstants.Users.NEW_VALID_USER).setLogin(null)
        ), HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_LOGIN_NULL);

        // Check the user was not created in the database

        int nbInDbAfterTest = this.userRepository.findAll().size();

        Assertions.assertEquals(nbInDbBeforeTest, nbInDbAfterTest);
    }

    /**
     * Test an invalid register because empty login
     */
    @Test
    void testInvalidRegisterBecauseEmptyLogin() {
        int nbInDbBeforeTest = this.userRepository.findAll().size();

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.register(
            UserConstants.Users.getCopy(UserConstants.Users.NEW_VALID_USER).setLogin("")
        ), HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_LOGIN_NULL);

        // Check the user was not created in the database

        int nbInDbAfterTest = this.userRepository.findAll().size();

        Assertions.assertEquals(nbInDbBeforeTest, nbInDbAfterTest);
    }

    /**
     * Test an invalid register because invalid login
     */
    @ParameterizedTest(name = "#{index} - Run test with username = {0}")
    @MethodSource("invalidLoginProvider")
    void testInvalidRegisterBecauseInvalidLogin(String login) {
        int nbInDbBeforeTest = this.userRepository.findAll().size();

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.register(
            UserConstants.Users.getCopy(UserConstants.Users.NEW_VALID_USER).setLogin(login)
        ), HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_LOGIN_INVALID);

        // Check the user was not created in the database

        int nbInDbAfterTest = this.userRepository.findAll().size();

        Assertions.assertEquals(nbInDbBeforeTest, nbInDbAfterTest);
    }

    /**
     * Test an invalid register because login already exists
     */
    @Test
    void testInvalidRegisterBecauseAlreadyExistsLogin() {
        int nbInDbBeforeTest = this.userRepository.findAll().size();

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.register(
            UserConstants.Users.getCopy(UserConstants.Users.NEW_VALID_USER).setLogin(UserConstants.Users.ACTIVATED.getLogin())
        ), HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_LOGIN_ALREADY_EXISTS);

        // Check the user was not created in the database

        int nbInDbAfterTest = this.userRepository.findAll().size();

        Assertions.assertEquals(nbInDbBeforeTest, nbInDbAfterTest);
    }

    /**
     * Test an invalid register because no password
     */
    @Test
    void testInvalidRegisterBecauseNoPassword() {
        int nbInDbBeforeTest = this.userRepository.findAll().size();

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.register(
            UserConstants.Users.getCopy(UserConstants.Users.NEW_VALID_USER).setPassword(null)
        ), HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_PASSWORD_NULL);

        // Check the user was not created in the database

        int nbInDbAfterTest = this.userRepository.findAll().size();

        Assertions.assertEquals(nbInDbBeforeTest, nbInDbAfterTest);
    }

    /**
     * Test an invalid register because empty password
     */
    @Test
    void testInvalidRegisterBecauseEmptyPassword() {
        int nbInDbBeforeTest = this.userRepository.findAll().size();

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.register(
            UserConstants.Users.getCopy(UserConstants.Users.NEW_VALID_USER).setPassword("")
        ), HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_PASSWORD_NULL);

        // Check the user was not created in the database

        int nbInDbAfterTest = this.userRepository.findAll().size();

        Assertions.assertEquals(nbInDbBeforeTest, nbInDbAfterTest);
    }

    /**
     * Test an invalid register because invalid password
     */
    @ParameterizedTest(name = "#{index} - Run test with password = {0}")
    @MethodSource("invalidPasswordProvider")
    void testInvalidRegisterBecauseInvalidPassword(String password) {
        int nbInDbBeforeTest = this.userRepository.findAll().size();

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.register(
            UserConstants.Users.getCopy(UserConstants.Users.NEW_VALID_USER).setPassword(password)
        ), HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_PASSWORD_INVALID);

        // Check the user was not created in the database

        int nbInDbAfterTest = this.userRepository.findAll().size();

        Assertions.assertEquals(nbInDbBeforeTest, nbInDbAfterTest);
    }

    /**
     * Test a valid logout
     */
    @Test
    void testValidLogout() {
        UserAuthenticatorModel userAuthenticator = new UserAuthenticatorModel()
            .setLoginOrEmail(UserConstants.Users.ACTIVATED.getEmail())
            .setPassword(UserConstants.Users.ACTIVATED.getPassword())
            .setRememberMe(true);

        String token = new HttpResponseAssert<>(this.userResource.login(
            DEFAULT_USER_AGENT,
            DEFAULT_CLIENT_IP,
            userAuthenticator
        )).assertHttpStatus(HttpStatus.OK)
            .assertNbHeaders(0)
            .getNotNullBody().getToken();

        // Check if response is valid

        new HttpResponseAssert<>(this.userResource.logout(token))
            .assertHttpStatus(HttpStatus.OK)
            .assertNbHeaders(0)
            .assertNullBody();

        // Check if token in database is now disabled

        List<TokenEntity> tokens = this.tokenRepository.findAll();

        Assertions.assertEquals(1, tokens.size());

        TokenEntity createdToken = tokens.get(0);

        Assertions.assertEquals(UserConstants.Users.ACTIVATED.getId(), createdToken.getId());
        Assertions.assertEquals(token, createdToken.getValue());
        Assertions.assertNotNull(createdToken.getCreatedAt());
        Assertions.assertTrue(createdToken.getDeleted());
    }

    /**
     * Test an invalid logout because no token provided
     */
    @Test
    void testInvalidLogoutBecauseNoTokenProvided() {
        // Check if response is valid

        HttpResponseAssert.AssertRestException(
            () -> this.userResource.logout(null),
            HttpStatus.BAD_REQUEST,
            ErrorKeys.User.LOGOUT_TOKEN_NULL
        );

        // Check if token in database is now disabled

        List<TokenEntity> tokens = this.tokenRepository.findAll();

        Assertions.assertEquals(0, tokens.size());
    }

    /**
     * Test an invalid logout because non existant token provided
     */
    @Test
    void testInvalidLogoutBecauseNoExistantTokenProvided() {
        // Check if response is valid

        HttpResponseAssert.AssertRestException(
            () -> this.userResource.logout("non_existant_token"),
            HttpStatus.BAD_REQUEST,
            ErrorKeys.User.LOGOUT_TOKEN_NOT_EXISTS
        );

        // Check if token in database is now disabled

        List<TokenEntity> tokens = this.tokenRepository.findAll();

        Assertions.assertEquals(0, tokens.size());
    }
}
