package fr.koi.testapi.resource.user;

import fr.koi.testapi.model.user.JwtTokenModel;
import fr.koi.testapi.model.user.UserAuthenticator;
import fr.koi.testapi.model.user.UserRegister;
import fr.koi.testapi.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Web resource to manage users
 */
@RestController
@RequestMapping(path = "/api/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserResource {
    /**
     * The service to manage users
     */
    private final UserService userService;

    /**
     * Create a new web resource to manage users
     *
     * @param userService The service to manage users
     */
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    /**
     * Perform a login
     *
     * @param userAgent     The user agent
     * @param clientIp      The client IP
     * @param authenticator The user authenticator
     *
     * @return The created JWT token
     */
    @PostMapping("/login")
    @SuppressWarnings("java:S2143")
    public ResponseEntity<JwtTokenModel> login(
        @RequestHeader("User-Agent") String userAgent,
        @RequestHeader("X-Forwarded-For") String clientIp,
        @RequestBody UserAuthenticator authenticator
    ) {
        return ResponseEntity.ok(this.userService.login(authenticator, userAgent, clientIp));
    }

    /**
     * Perform a register
     *
     * @param userRegister The user register model
     *
     * @return Empty HTTP response
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserRegister userRegister) {
        this.userService.register(userRegister);

        return ResponseEntity.ok(null);
    }
}
