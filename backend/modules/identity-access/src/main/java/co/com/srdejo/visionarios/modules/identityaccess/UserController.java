package co.com.srdejo.visionarios.modules.identityaccess;

import co.com.srdejo.visionarios.modules.identityaccess.dto.UserResponse;
import co.com.srdejo.visionarios.platform.webcommon.ApiResponse;
import co.com.srdejo.visionarios.platform.webcommon.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public UserController(UserRepository userRepository, CurrentUser currentUser) {
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me() {
        User user = userRepository.findById(currentUser.id())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return ApiResponse.ok(UserResponse.from(user));
    }
}
