package co.com.srdejo.visionarios.modules.admin;

import co.com.srdejo.visionarios.modules.admin.dto.AdminUsersResponse;
import co.com.srdejo.visionarios.modules.identityaccess.Profile;
import co.com.srdejo.visionarios.modules.identityaccess.UserRepository;
import co.com.srdejo.visionarios.modules.identityaccess.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AdminUsersService {

    private final UserRepository userRepository;

    public AdminUsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public AdminUsersResponse findAll() {
        var users = userRepository.findAll().stream().map(UserResponse::from).toList();
        Map<String, Long> countByProfile = new LinkedHashMap<>();
        for (Profile profile : Profile.values()) {
            countByProfile.put(profile.name(), userRepository.countByProfile(profile));
        }
        return new AdminUsersResponse(users, users.size(), countByProfile);
    }
}
