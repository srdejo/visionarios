package co.com.srdejo.visionarios.modules.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminInviteRepository extends JpaRepository<AdminInvite, UUID> {

    Optional<AdminInvite> findByToken(String token);

    List<AdminInvite> findAllByOrderByCreatedAtDesc();
}
