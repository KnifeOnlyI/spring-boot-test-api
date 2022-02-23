package fr.koi.testapi.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import fr.koi.testapi.dto.JwtTokenDTO;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Service to manage JWT tokens
 */
@Service
public class JwtService {
    private final Algorithm algorithm;

    /**
     * Create a new JWT service
     *
     * @param environment The environment
     */
    public JwtService(Environment environment) {
        this.algorithm = Algorithm.HMAC256(environment.getProperty("jwt.secret"));
    }

    public String create(String name, String userAgent, String ip, Date creationDate, Date expirationDate) {
        Map<String, Object> header = new HashMap<>();
        Map<String, Object> payload = new HashMap<>();

        header.put("alg", "HS256");
        header.put("typ", "JWT");
        payload.put("sub", name);
        payload.put("user_agent", userAgent);
        payload.put("ip", ip);
        payload.put("iat", creationDate);
        payload.put("exp", expirationDate);

        return JWT.create()
            .withHeader(header)
            .withPayload(payload)
            .sign(algorithm);
    }

    public JwtTokenDTO verify(String token) {
        return new JwtTokenDTO(JWT.decode(token));
    }
}
