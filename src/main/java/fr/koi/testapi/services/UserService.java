package fr.koi.testapi.services;

import fr.koi.testapi.constants.ErrorKeys;
import fr.koi.testapi.domain.TokenEntity;
import fr.koi.testapi.domain.UserEntity;
import fr.koi.testapi.dto.JwtTokenDTO;
import fr.koi.testapi.exception.RestException;
import fr.koi.testapi.mapper.UserMapper;
import fr.koi.testapi.model.user.JwtTokenModel;
import fr.koi.testapi.model.user.UserAuthenticatorModel;
import fr.koi.testapi.model.user.UserRegisterModel;
import fr.koi.testapi.repository.TokenRepository;
import fr.koi.testapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * The service to manage users
 */
@Service
public class UserService {
    /**
     * The REGEX pattern for valid email
     */
    @SuppressWarnings("java:S3749")
    private final Pattern validEmailRegex = Pattern.compile("^(.+)@(\\S+)$", Pattern.CASE_INSENSITIVE);

    /**
     * The REGEX pattern for valid login
     */
    @SuppressWarnings("java:S3749")
    private final Pattern validLoginRegex = Pattern.compile(
        "^[A-Z0-9]([._-](?![._-])|[A-Z0-9]){3,18}[A-Z0-9]$",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * The REGEX pattern for valid password
     */
    @SuppressWarnings({"java:S3749", "java:S4248"})
    private final Pattern validPasswordRegex = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–{}:;',?/*~$^+=<>]).{8,255}$"
    );


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
     * The mapper for users
     */
    private final UserMapper userMapper;

    /**
     * Create a new service to manage users
     *
     * @param userRepository  The repository to manage users
     * @param passwordService The service to manage passwords
     * @param jwtService      The service to manage JWT tokens
     * @param tokenRepository The repository to manage tokens
     * @param userMapper      The mapper for users
     */
    public UserService(
        UserRepository userRepository,
        PasswordService passwordService,
        JwtService jwtService,
        TokenRepository tokenRepository,
        UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.userMapper = userMapper;
    }

    /**
     * Assert the specified authenticator is valid
     *
     * @param authenticator The user authenticator
     */
    private UserEntity assertUserIsActivatedAndPasswordMatches(UserAuthenticatorModel authenticator) {
        String loginOrEmail = authenticator.getLoginOrEmail() == null ? "" : authenticator.getLoginOrEmail();

        UserEntity user = this.userRepository.findByLoginOrEmail(loginOrEmail, loginOrEmail).orElse(null);

        // Check if passwords matches and is user is activated
        if (!(user != null &&
            Boolean.TRUE.equals(this.passwordService.matches(authenticator.getPassword(), user.getPassword())))
        ) {
            throw new RestException(HttpStatus.UNAUTHORIZED, ErrorKeys.User.LOGIN_INVALID_CREDENTIALS);
        } else if (Boolean.FALSE.equals(user.getActivated())) {
            throw new RestException(HttpStatus.UNAUTHORIZED, ErrorKeys.User.LOGIN_DESACTIVATED);
        }

        return user;
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
    @SuppressWarnings("java:S2143")
    private JwtTokenModel createNewJwtToken(
        UserAuthenticatorModel authenticator,
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
    @Transactional
    @SuppressWarnings("java:S2143")
    public JwtTokenModel login(UserAuthenticatorModel authenticator, String userAgent, String clientIp) {
        if (userAgent == null || "".equalsIgnoreCase(userAgent.strip())) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.User.LOGIN_HEADER_USER_AGENT_NULL);
        } else if (clientIp == null || "".equalsIgnoreCase(clientIp.strip())) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.User.LOGIN_HEADER_X_FORWARDED_FOR_NULL);
        }

        UserEntity user = this.assertUserIsActivatedAndPasswordMatches(authenticator);
        Date currentDate = new Date();

        this.deletePreviousIdenticalTokens(user, userAgent, clientIp, currentDate);

        return this.createNewJwtToken(authenticator, user, userAgent, clientIp, currentDate);
    }

    /**
     * Perform a register
     *
     * @param userRegister The user register model
     */
    @Transactional
    @SuppressWarnings("java:S2143")
    public void register(UserRegisterModel userRegister) {
        this.checkEmail(userRegister.getEmail());
        this.checkLogin(userRegister.getLogin());
        this.checkPassword(userRegister.getPassword());

        UserEntity user = this.userRepository.findByLoginOrEmail(
            userRegister.getLogin(),
            userRegister.getEmail()
        ).orElse(null);

        if (user != null && user.getEmail().equals(userRegister.getEmail())) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_EMAIL_ALREADY_EXISTS);
        } else if (user != null && user.getLogin().equals(userRegister.getLogin())) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_LOGIN_ALREADY_EXISTS);
        }

        userRegister.setEmail(userRegister.getEmail().toLowerCase(Locale.ROOT));
        userRegister.setLogin(userRegister.getLogin().toLowerCase(Locale.ROOT));

        this.userRepository.save(this.userMapper.toEntity(userRegister).setCreatedAt(new Date()).setActivated(false));
    }

    /**
     * Check if the specified email is valid
     *
     * @param email The email to check
     */
    private void checkEmail(String email) {
        if (email == null || "".equals(email.strip())) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_EMAIL_NULL);
        } else if (!this.validEmailRegex.matcher(email).find()) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_EMAIL_INVALID);
        }
    }

    /**
     * Check if the specified login is valid
     *
     * @param login The login to check
     */
    private void checkLogin(String login) {
        if (login == null || "".equals(login.strip())) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_LOGIN_NULL);
        } else if (!this.validLoginRegex.matcher(login).find()) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_LOGIN_INVALID);
        }
    }

    /**
     * Check if the specified password is valid
     *
     * @param password The password to check
     */
    private void checkPassword(String password) {
        if (password == null || "".equals(password.strip())) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_PASSWORD_NULL);
        } else if (!this.validPasswordRegex.matcher(password).find()) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.User.REGISTER_PASSWORD_INVALID);
        }
    }
}
