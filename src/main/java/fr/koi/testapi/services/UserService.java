package fr.koi.testapi.services;

import fr.koi.testapi.constants.ErrorKeys;
import fr.koi.testapi.domain.GroupEntity;
import fr.koi.testapi.domain.PermissionEntity;
import fr.koi.testapi.domain.TokenEntity;
import fr.koi.testapi.domain.UserEntity;
import fr.koi.testapi.dto.JwtTokenDTO;
import fr.koi.testapi.exception.RestException;
import fr.koi.testapi.mapper.UserMapper;
import fr.koi.testapi.repository.TokenRepository;
import fr.koi.testapi.repository.UserRepository;
import fr.koi.testapi.web.model.user.JwtTokenModel;
import fr.koi.testapi.web.model.user.UserAuthenticatorModel;
import fr.koi.testapi.web.model.user.UserModel;
import fr.koi.testapi.web.model.user.UserRegisterModel;
import fr.koi.testapi.web.model.user.UserUpdateEmailLoginModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The service to manage users
 */
@Service
public class UserService {
    /**
     * The REGEX pattern for valid header authorization
     */
    private static final Pattern validBearerHeader = Pattern.compile(
        "^Bearer\s[a-zA-Z0-9-_]+\\.[a-zA-Z0-9-_]+\\.[a-zA-Z0-9-_]+$"
    );

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
        if (user.getTokens() != null) {
            user.getTokens().forEach((TokenEntity tokenEntity) -> {
                JwtTokenDTO tokenDTO = jwtService.verify(tokenEntity.getValue());

                if ((tokenDTO.getExpirationDate() != null && tokenDTO.getExpirationDate().before(currentDate)) ||
                    (tokenDTO.getUserAgent().equals(userAgent) && tokenDTO.getClientIp().equalsIgnoreCase(clientIp))
                ) {
                    tokenEntity.setDeleted(true);
                }
            });
        }
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

        if (user.getTokens() == null) {
            user.setTokens(new ArrayList<>());
        }

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
        this.checkEmail(
            userRegister.getEmail(), ErrorKeys.User.REGISTER_EMAIL_NULL, ErrorKeys.User.REGISTER_EMAIL_INVALID
        );

        this.checkLogin(
            userRegister.getLogin(), ErrorKeys.User.REGISTER_LOGIN_NULL, ErrorKeys.User.REGISTER_LOGIN_INVALID
        );

        this.checkPassword(
            userRegister.getPassword(), ErrorKeys.User.REGISTER_PASSWORD_NULL, ErrorKeys.User.REGISTER_PASSWORD_INVALID
        );

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
        userRegister.setPassword(this.passwordService.encode(userRegister.getPassword()));

        this.userRepository.save(this.userMapper.toEntity(userRegister));
    }

    /**
     * Perform an update of email and/or login of the specified user
     *
     * @param authorization The authorization header
     * @param model         The update model
     *
     * @return The updated user
     */
    @Transactional
    public UserModel updateEmailOrLogin(String authorization, UserUpdateEmailLoginModel model) {
        UserEntity userAuthorization = this.getUserOfAuthorization(authorization);
        UserEntity userToUpdate = this.userRepository.findById(model.getId())
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, ErrorKeys.User.NOT_EXISTS));

        checkUpdateData(model, userAuthorization, userToUpdate);

        if (model.getEmail() != null) {
            userToUpdate.setEmail(model.getEmail());
        }

        if (model.getLogin() != null) {
            userToUpdate.setLogin(model.getLogin());
        }

        if (model.getLogin() == null && model.getEmail() == null) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.User.UPDATE_EMAIL_AND_LOGIN_NULL);
        }

        return this.userMapper.toModel(userToUpdate);
    }

    /**
     * Check the validity of the specified update data
     *
     * @param model             The update data to check
     * @param userAuthorization The user entity from authorization header
     * @param userToUpdate      The user entity to update
     */
    private void checkUpdateData(
        UserUpdateEmailLoginModel model, UserEntity userAuthorization, UserEntity userToUpdate
    ) {
        if (!userAuthorization.getId().equals(userToUpdate.getId())) {
            throw new RestException(HttpStatus.UNAUTHORIZED, ErrorKeys.Authorization.NOT_AUTHORIZED);
        }

        if (model.getId() == null) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.User.UPDATE_ID_NULL);
        }

        if (model.getEmail() != null && !"".equalsIgnoreCase(model.getEmail())) {
            this.checkEmail(model.getEmail(), ErrorKeys.User.UPDATE_EMAIL_INVALID, ErrorKeys.User.UPDATE_EMAIL_INVALID);
        } else {
            model.setEmail(null);
        }

        if (model.getLogin() != null && !"".equalsIgnoreCase(model.getLogin())) {
            this.checkLogin(model.getLogin(), ErrorKeys.User.UPDATE_LOGIN_INVALID, ErrorKeys.User.UPDATE_LOGIN_INVALID);
        } else {
            model.setLogin(null);
        }
    }

    /**
     * Get the token value of the specified authorization header
     *
     * @param authorization The authorization header
     *
     * @return The token value
     */
    @SuppressWarnings("java:S109")
    private static String getTokenValueOfAuthorization(String authorization) {
        if (authorization == null) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.Authorization.HEADER_NULL);
        }

        if (!validBearerHeader.matcher(authorization).find()) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.Authorization.HEADER_INVALID);
        }

        return authorization.split(" ")[1];
    }

    /**
     * Perform a logout
     *
     * @param authorization The authorization that contains the token to delete
     */
    public void logout(String authorization) {
        this.tokenRepository.save(getTokenOfAuthorization(authorization).setDeleted(true));
    }

    /**
     * Assert the user in the specified authorization header has the specified permissions
     *
     * @param authorization       The authorization header
     * @param expectedPermissions The expected permissions
     */
    @SuppressWarnings("java:S923")
    public void assertHasPermission(String authorization, String... expectedPermissions) {
        UserEntity user = this.getUserOfAuthorization(authorization);
        Map<String, Boolean> userPermissions = new HashMap<>();

        // Get all user permissions list
        for (GroupEntity group : user.getGroups()) {
            for (PermissionEntity permission : group.getPermissions()) {
                userPermissions.put(permission.getName().toLowerCase(Locale.ROOT), true);
            }
        }

        // Search if the user have all expected permissions
        for (String permission : expectedPermissions) {
            if (userPermissions.get(permission.toLowerCase(Locale.ROOT)) == null) {
                throw new RestException(HttpStatus.UNAUTHORIZED, ErrorKeys.Authorization.NOT_AUTHORIZED);
            }
        }
    }

    /**
     * Get the user of the specified authorization header
     *
     * @param authorization The authorization header
     *
     * @return The founded user
     */
    @SuppressWarnings("java:S2143")
    public UserEntity getUserOfAuthorization(String authorization) {
        TokenEntity tokenEntity = this.getTokenOfAuthorization(authorization);
        JwtTokenDTO jwtTokenDTO = this.jwtService.verify(tokenEntity.getValue());

        if (jwtTokenDTO.getExpirationDate() != null && jwtTokenDTO.getExpirationDate().before(new Date())) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.Authorization.TOKEN_NOT_EXISTS);
        }

        UserEntity user = this.userRepository.findById(jwtTokenDTO.getUserId())
            .orElseThrow(() -> new RestException(HttpStatus.UNAUTHORIZED, ErrorKeys.User.NOT_EXISTS));

        if (Boolean.FALSE.equals(user.getActivated())) {
            throw new RestException(HttpStatus.UNAUTHORIZED, ErrorKeys.User.NOT_ACTIVATED);
        }

        return user;
    }

    /**
     * Get the token entity in the database corresponding to the specified authorization header
     *
     * @param authorization The authorization header
     *
     * @return The corresponding token entity
     */
    public TokenEntity getTokenOfAuthorization(String authorization) {
        TokenEntity token = this.tokenRepository.getTokenByValue(getTokenValueOfAuthorization(authorization))
            .orElse(null);

        if (token == null || Boolean.TRUE.equals(token.getDeleted())) {
            throw new RestException(HttpStatus.BAD_REQUEST, ErrorKeys.Authorization.TOKEN_NOT_EXISTS);
        }

        return token;
    }

    /**
     * Check if the specified email is valid
     *
     * @param email           The email to check
     * @param errorForNull    The error when null
     * @param errorForInvalid The error when invalid
     */
    private void checkEmail(String email, String errorForNull, String errorForInvalid) {
        if (email == null || "".equals(email.strip())) {
            throw new RestException(HttpStatus.BAD_REQUEST, errorForNull);
        } else if (!this.validEmailRegex.matcher(email).find()) {
            throw new RestException(HttpStatus.BAD_REQUEST, errorForInvalid);
        }
    }

    /**
     * Check if the specified login is valid
     *
     * @param login           The login to check
     * @param errorForNull    The error when null
     * @param errorForInvalid The error when invalid
     */
    private void checkLogin(String login, String errorForNull, String errorForInvalid) {
        if (login == null || "".equals(login.strip())) {
            throw new RestException(HttpStatus.BAD_REQUEST, errorForNull);
        } else if (!this.validLoginRegex.matcher(login).find()) {
            throw new RestException(HttpStatus.BAD_REQUEST, errorForInvalid);
        }
    }

    /**
     * Check if the specified password is valid
     *
     * @param password        The password to check
     * @param errorForNull    The error when null
     * @param errorForInvalid The error when invalid
     */
    private void checkPassword(String password, String errorForNull, String errorForInvalid) {
        if (password == null || "".equals(password.strip())) {
            throw new RestException(HttpStatus.BAD_REQUEST, errorForNull);
        } else if (!this.validPasswordRegex.matcher(password).find()) {
            throw new RestException(HttpStatus.BAD_REQUEST, errorForInvalid);
        }
    }
}
