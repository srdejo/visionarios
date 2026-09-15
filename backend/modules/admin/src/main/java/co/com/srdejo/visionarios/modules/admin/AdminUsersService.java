package co.com.srdejo.visionarios.modules.admin;

import co.com.srdejo.visionarios.modules.admin.dto.AdminUsersResponse;
import co.com.srdejo.visionarios.modules.identityaccess.BusinessCategory;
import co.com.srdejo.visionarios.modules.identityaccess.Profile;
import co.com.srdejo.visionarios.modules.identityaccess.UserRepository;
import co.com.srdejo.visionarios.modules.identityaccess.UserSpecifications;
import co.com.srdejo.visionarios.modules.identityaccess.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public AdminUsersResponse search(String search, Profile profile, BusinessCategory businessCategory,
                                      Integer minAge, Integer maxAge, Integer minEmployees, Integer maxEmployees,
                                      int page, int size) {
        long total = userRepository.count();
        Map<String, Long> countByProfile = new LinkedHashMap<>();
        for (Profile p : Profile.values()) {
            countByProfile.put(p.name(), userRepository.countByProfile(p));
        }

        var spec = UserSpecifications.withFilters(search, profile, businessCategory, minAge, maxAge, minEmployees, maxEmployees);
        Page<UserResponse> result = userRepository
                .findAll(spec, PageRequest.of(page, size, Sort.by("fullName").ascending()))
                .map(UserResponse::from);

        return new AdminUsersResponse(result.getContent(), total, countByProfile,
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }
}
