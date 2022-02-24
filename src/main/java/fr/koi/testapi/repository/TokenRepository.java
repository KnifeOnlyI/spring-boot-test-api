package fr.koi.testapi.repository;

import fr.koi.testapi.domain.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The repository to manage tokens in database
 */
@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    /**
     * Find a token by a specified value
     *
     * @param value The token value to search
     *
     * @return The founded token
     */
    Optional<TokenEntity> getTokenByValue(String value);
}
