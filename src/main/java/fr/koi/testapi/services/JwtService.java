package fr.koi.testapi.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import fr.koi.testapi.dto.JwtTokenDTO;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The service to manage JWT tokens
 */
@Service
public class JwtService {
    /**
     * The algorithm to generate/verify tokens
     */
    private final Algorithm algorithm;

    /**
     * Create a new JWT service
     *
     * @param environment The environment
     */
    public JwtService(Environment environment) {
        this.algorithm = Algorithm.HMAC256(environment.getProperty("jwt.secret"));
    }

    /**
     * Create a new JWT token
     *
     * @param userId         The user userId
     * @param userAgent      The user agent
     * @param clientIp       The client IP
     * @param expirationDate The expiration date
     *
     * @return The created token
     */
    public String create(Long userId, String userAgent, String clientIp, Date expirationDate) {
        Map<String, Object> header = new HashMap<>();
        Map<String, Object> payload = new HashMap<>();

        header.put("alg", "HS256");
        header.put("typ", "JWT");
        payload.put("sub", userId);
        payload.put("uag", userAgent);
        payload.put("cip", clientIp);
        payload.put("exp", expirationDate);

        return JWT.create()
            .withHeader(header)
            .withPayload(payload)
            .sign(this.algorithm);
    }

    /**
     * Check if the specified token is valid and return a DTO representation of the
     *
     * @param token The token to check
     *
     * @return A DTO representation of the specified token
     */
    public JwtTokenDTO verify(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);

        Long sub = decodedJWT.getClaim("sub").asLong();
        String uag = decodedJWT.getClaim("uag").asString();
        String cip = decodedJWT.getClaim("cip").asString();
        Date exp = decodedJWT.getClaim("exp").asDate();

        // Recreate a token with the received data to check the received token validity
        if (!token.equals(this.create(sub, uag, cip, exp))) {
            throw new JWTVerificationException("Not valid token");
        }

        return new JwtTokenDTO()
            .setUserId(sub)
            .setUserAgent(uag)
            .setClientIp(cip)
            .setExpirationDate(exp);
    }
}
