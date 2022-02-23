package fr.koi.testapi.repository;

import fr.koi.testapi.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    /**
     * Find a token by a specified value
     *
     * @param value The token value to search
     *
     * @return The founded token
     */
    Optional<Token> getTokenByValue(String value);
}
