package fr.koi.testapi.resource.user;

import fr.koi.testapi.constants.UserConstants;
import fr.koi.testapi.domain.TokenEntity;
import fr.koi.testapi.dto.JwtTokenDTO;
import fr.koi.testapi.model.user.JwtTokenModel;
import fr.koi.testapi.model.user.UserAuthenticator;
import fr.koi.testapi.repository.TokenRepository;
import fr.koi.testapi.services.JwtService;
import fr.koi.testapi.util.HttpResponseAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Date;
import java.util.List;

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

    @Autowired
    private JwtService jwtService;

    /**
     * Test a valid login with email and remember me
     */
    @Test
    void testValidLoginWithEmailAndRememberMe() {
        UserAuthenticator userAuthenticator = new UserAuthenticator()
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
        UserAuthenticator userAuthenticator = new UserAuthenticator()
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
        UserAuthenticator userAuthenticator = new UserAuthenticator()
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
        UserAuthenticator userAuthenticator = new UserAuthenticator()
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
        UserAuthenticator userAuthenticator = new UserAuthenticator()
            .setLoginOrEmail(null)
            .setPassword(null)
            .setRememberMe(false);

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.login(
            DEFAULT_USER_AGENT, DEFAULT_CLIENT_IP, userAuthenticator
        ), HttpStatus.UNAUTHORIZED, "error.login.invalidCredentials");
    }

    /**
     * Test an invalid login because invalid login / password without remember me
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testInvalidLoginWithInvalidCredentialsLoginPasswordBecauseInvalidCredentials() {
        UserAuthenticator userAuthenticator = new UserAuthenticator()
            .setLoginOrEmail(null)
            .setPassword(null)
            .setRememberMe(false);

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.login(
            DEFAULT_USER_AGENT, DEFAULT_CLIENT_IP, userAuthenticator
        ), HttpStatus.UNAUTHORIZED, "error.login.invalidCredentials");
    }

    /**
     * Test an invalid login with valid desactivated email and invalid password without remember me
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testInvalidLoginWithValidEmailAndInvalidPasswordAndDesactivated() {
        UserAuthenticator userAuthenticator = new UserAuthenticator()
            .setLoginOrEmail(UserConstants.Users.DESACTIVATED.getEmail())
            .setPassword(null)
            .setRememberMe(false);

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.login(
            DEFAULT_USER_AGENT, DEFAULT_CLIENT_IP, userAuthenticator
        ), HttpStatus.UNAUTHORIZED, "error.login.invalidCredentials");
    }

    /**
     * Test an invalid login with valid desactivated login and invalid password without remember me
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testInvalidLoginWithLoginAndInvalidPasswordAndDesactivated() {
        UserAuthenticator userAuthenticator = new UserAuthenticator()
            .setLoginOrEmail(UserConstants.Users.DESACTIVATED.getLogin())
            .setPassword(null)
            .setRememberMe(false);

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.login(
            DEFAULT_USER_AGENT, DEFAULT_CLIENT_IP, userAuthenticator
        ), HttpStatus.UNAUTHORIZED, "error.login.invalidCredentials");
    }

    /**
     * Test an invalid login with valid desactivated email and password without remember me
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testInvalidLoginWithValidEmailAndPasswordBecauseDesactivated() {
        UserAuthenticator userAuthenticator = new UserAuthenticator()
            .setLoginOrEmail(UserConstants.Users.DESACTIVATED.getEmail())
            .setPassword(UserConstants.Users.DESACTIVATED.getPassword())
            .setRememberMe(false);

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.login(
            DEFAULT_USER_AGENT, DEFAULT_CLIENT_IP, userAuthenticator
        ), HttpStatus.UNAUTHORIZED, "error.login.desactivatedUser");
    }

    /**
     * Test an invalid login with valid desactivated login and password without remember me
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testInvalidLoginWithValidLoginAndPasswordBecauseDesactivated() {
        UserAuthenticator userAuthenticator = new UserAuthenticator()
            .setLoginOrEmail(UserConstants.Users.DESACTIVATED.getLogin())
            .setPassword(UserConstants.Users.DESACTIVATED.getPassword())
            .setRememberMe(false);

        // Check if response is invalid

        HttpResponseAssert.AssertRestException(() -> this.userResource.login(
            DEFAULT_USER_AGENT, DEFAULT_CLIENT_IP, userAuthenticator
        ), HttpStatus.UNAUTHORIZED, "error.login.desactivatedUser");
    }
}
