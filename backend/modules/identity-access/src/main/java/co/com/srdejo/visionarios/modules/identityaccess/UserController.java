package co.com.srdejo.visionarios.modules.identityaccess;

import co.com.srdejo.visionarios.modules.identityaccess.dto.UpdateProfileRequest;
import co.com.srdejo.visionarios.modules.identityaccess.dto.UserResponse;
import co.com.srdejo.visionarios.platform.webcommon.ApiResponse;
import co.com.srdejo.visionarios.platform.webcommon.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final CurrentUser currentUser;
    private final AuthService authService;

    public UserController(UserRepository userRepository, CurrentUser currentUser, AuthService authService) {
        this.userRepository = userRepository;
        this.currentUser = currentUser;
        this.authService = authService;
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me() {
        User user = userRepository.findById(currentUser.id())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return ApiResponse.ok(UserResponse.from(user));
    }

    @PutMapping("/me")
    public ApiResponse<UserResponse> updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.ok(authService.updateProfile(currentUser.id(), request));
    }
}
