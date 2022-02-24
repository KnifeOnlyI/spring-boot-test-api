package fr.koi.testapi.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * The service to manage passwords
 */
@Service
public class PasswordService {
    /**
     * The password encoder
     */
    @SuppressWarnings("java:S3749")
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(16);

    /**
     * Encode the specified raw password
     *
     * @param raw The raw password to encode
     *
     * @return The encoded password
     */
    public String encode(String raw) {
        return this.passwordEncoder.encode(raw);
    }

    /**
     * Check the match between specified raw and encoded passwords
     *
     * @param raw The raw password
     * @param encoded The encoded password
     *
     * @return TRUE if the password matches, FALSE otherwise
     */
    public Boolean matches(String raw, String encoded) {
        return this.passwordEncoder.matches(raw == null ? "" : raw, encoded == null ? "" : encoded);
    }
}
