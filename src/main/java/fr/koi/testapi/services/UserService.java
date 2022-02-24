package fr.koi.testapi.services;

import fr.koi.testapi.domain.TokenEntity;
import fr.koi.testapi.domain.UserEntity;
import fr.koi.testapi.dto.JwtTokenDTO;
import fr.koi.testapi.exception.RestException;
import fr.koi.testapi.model.user.JwtTokenModel;
import fr.koi.testapi.model.user.UserAuthenticator;
import fr.koi.testapi.repository.TokenRepository;
import fr.koi.testapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * The service to manage users
 */
@Service
public class UserService {
    /**
     * The repository to manage users
     */
    private final UserRepository userRepository;

    /**
     * The service to manage passwords
     */
    private final PasswordService passwordService;

    /**
     * The service to manage JWT tokens
     */
    private final JwtService jwtService;

    /**
     * The repository to manage tokens
     */
    private final TokenRepository tokenRepository;

    /**
     * Create a new service to manage users
     *
     * @param userRepository  The repository to manage users
     * @param passwordService The service to manage passwords
     * @param jwtService      The service to manage JWT tokens
     * @param tokenRepository The repository to manage tokens
     */
    public UserService(
        UserRepository userRepository,
        PasswordService passwordService,
        JwtService jwtService,
        TokenRepository tokenRepository
    ) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
    }

    /**
     * Perform a logical deletion of the previous tokens has the specified data
     *
     * @param user        The owner of tokens to check
     * @param userAgent   The user agent header value
     * @param clientIp    The client IP header value
     * @param currentDate The current date
     */
    private void deletePreviousIdenticalTokens(UserEntity user, String userAgent, String clientIp, Date currentDate) {
        // Delete expired tokens
        // Delete previous tokens with same user agent and client IP
        user.getTokens().forEach((TokenEntity tokenEntity) -> {
            JwtTokenDTO tokenDTO = jwtService.verify(tokenEntity.getValue());

            if ((tokenDTO.getExpirationDate() != null && tokenDTO.getExpirationDate().before(currentDate)) ||
                (tokenDTO.getUserAgent().equals(userAgent) && tokenDTO.getClientIp().equalsIgnoreCase(clientIp))
            ) {
                tokenEntity.setDeleted(true);
            }
        });
    }

    /**
     * Create and save a new JWT token in the database for the specified user
     *
     * @param authenticator The user authenticator
     * @param user          The user to authenticate
     * @param userAgent     The user agent header value
     * @param clientIp      The client IP header value
     * @param currentDate   The current date
     *
     * @return The created JWT token model
     */
    private JwtTokenModel createNewJwtToken(
        UserAuthenticator authenticator,
        UserEntity user,
        String userAgent,
        String clientIp,
        Date currentDate
    ) {
        Date expirationDate = null;

        // Set the new token expiration date to h+1 if "remember me" field is not checked
        if (Boolean.FALSE.equals(authenticator.getRememberMe())) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.DAY_OF_WEEK, 1);
            expirationDate = cal.getTime();
        }

        JwtTokenModel token = new JwtTokenModel().setToken(this.jwtService.create(
            user.getId(),
            userAgent,
            clientIp,
            expirationDate
        ));

        user.getTokens().add(this.tokenRepository.save(new TokenEntity().setValue(token.getToken())));
        this.userRepository.save(user);

        return token;
    }

    /**
     * Perform a login
     *
     * @param authenticator The user authenticator
     * @param userAgent     The user agent
     * @param clientIp      The client IP
     *
     * @return The created JWT token
     */
    public JwtTokenModel login(UserAuthenticator authenticator, String userAgent, String clientIp) {
        String loginOrEmail = authenticator.getLoginOrEmail() == null ? "" : authenticator.getLoginOrEmail();

        UserEntity user = this.userRepository.findByLoginOrEmail(loginOrEmail, loginOrEmail).orElse(null);

        boolean passwordMatches = (user != null &&
            Boolean.TRUE.equals(this.passwordService.matches(authenticator.getPassword(), user.getPassword()))
        );

        boolean isActivated = user != null && Boolean.TRUE.equals(user.getActivated());

        if (passwordMatches && !isActivated) {
            throw new RestException(HttpStatus.UNAUTHORIZED, "error.login.desactivatedUser");
        } else if (!passwordMatches) {
            throw new RestException(HttpStatus.UNAUTHORIZED, "error.login.invalidCredentials");
        }

        Date currentDate = new Date();

        this.deletePreviousIdenticalTokens(user, userAgent, clientIp, currentDate);

        return this.createNewJwtToken(authenticator, user, userAgent, clientIp, currentDate);
    }
}
