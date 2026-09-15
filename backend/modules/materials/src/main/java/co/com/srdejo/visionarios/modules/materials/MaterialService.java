package co.com.srdejo.visionarios.modules.materials;

import co.com.srdejo.visionarios.modules.identityaccess.CurrentUser;
import co.com.srdejo.visionarios.modules.identityaccess.User;
import co.com.srdejo.visionarios.modules.identityaccess.UserRepository;
import co.com.srdejo.visionarios.modules.materials.dto.MaterialRequest;
import co.com.srdejo.visionarios.modules.materials.dto.MaterialResponse;
import co.com.srdejo.visionarios.platform.webcommon.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public MaterialService(MaterialRepository materialRepository, UserRepository userRepository, CurrentUser currentUser) {
        this.materialRepository = materialRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> findForCurrentUser() {
        User user = userRepository.findById(currentUser.id())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return materialRepository.findByVisibleProfileIsNullOrVisibleProfileOrderByCreatedAtDesc(user.getProfile())
                .stream().map(MaterialResponse::from).toList();
    }

    @Transactional
    public MaterialResponse create(MaterialRequest request) {
        Material material = new Material(UUID.randomUUID(), request.title(), request.type(), request.meta(),
                request.driveUrl(), request.visibleProfile());
        materialRepository.save(material);
        return MaterialResponse.from(material);
    }

    @Transactional
    public MaterialResponse update(UUID id, MaterialRequest request) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Material no encontrado"));
        material.update(request.title(), request.type(), request.meta(), request.driveUrl(), request.visibleProfile());
        return MaterialResponse.from(material);
    }
}
