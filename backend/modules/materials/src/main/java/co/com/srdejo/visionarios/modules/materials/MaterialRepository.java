package co.com.srdejo.visionarios.modules.materials;

import co.com.srdejo.visionarios.modules.identityaccess.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MaterialRepository extends JpaRepository<Material, UUID> {

    List<Material> findAllByOrderByCreatedAtDesc();

    List<Material> findByVisibleProfileIsNullOrVisibleProfileOrderByCreatedAtDesc(Profile profile);
}
