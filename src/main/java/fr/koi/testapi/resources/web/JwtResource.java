package fr.koi.testapi.resources.web;

import fr.koi.testapi.domain.Token;
import fr.koi.testapi.dto.JwtTokenDTO;
import fr.koi.testapi.repository.TokenRepository;
import fr.koi.testapi.resources.model.UserLogin;
import fr.koi.testapi.services.JwtService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class JwtResource {
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    public JwtResource(JwtService jwtService, TokenRepository tokenRepository) {
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
    }

    @GetMapping("/jwt/verify")
    public ResponseEntity<JwtTokenDTO> verify(@RequestHeader("Authorization") String authorization) {
        String jwtToken = authorization.split(" ")[1];

        if (this.tokenRepository.getTokenByValue(jwtToken).orElse(null) != null) {
            return ResponseEntity.ok(this.jwtService.verify(jwtToken));
        } else {
            return ResponseEntity.ok(null);
        }
    }

    @PostMapping("/jwt")
    public ResponseEntity<String> create(
        @RequestHeader("User-Agent") String userAgent,
        @RequestBody UserLogin userLogin
    ) {
        String token = this.jwtService.create(userLogin.getLogin(), userAgent, "127.0.0.1", new Date(), new Date());

        this.tokenRepository.save(new Token().setValue(token));

        return ResponseEntity.ok(token);
    }
}
