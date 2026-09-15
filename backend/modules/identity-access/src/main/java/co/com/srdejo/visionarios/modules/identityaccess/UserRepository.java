package co.com.srdejo.visionarios.modules.identityaccess;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByProfile(Profile profile);

    List<User> findByProfile(Profile profile);

    Optional<User> findByUnsubscribeToken(String unsubscribeToken);
}
