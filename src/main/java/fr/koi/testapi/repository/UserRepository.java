package fr.koi.testapi.repository;

import fr.koi.testapi.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The repository to manage users in database
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    /**
     * Search one user with corresponding (login or email)
     *
     * @param login The login to search
     * @param email The email to search
     *
     * @return The founded user
     */
    Optional<UserEntity> findByLoginOrEmail(String login, String email);
}
