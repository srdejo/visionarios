package co.com.srdejo.visionarios.modules.admin;

import co.com.srdejo.visionarios.modules.admin.dto.AcceptInviteRequest;
import co.com.srdejo.visionarios.modules.identityaccess.dto.AuthResponse;
import co.com.srdejo.visionarios.platform.webcommon.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Lives under /api/auth so it stays public per SecurityConfig's permitAll on "/api/auth/**". */
@RestController
@RequestMapping("/api/auth")
public class AcceptInviteController {

    private final AdminInviteService inviteService;

    public AcceptInviteController(AdminInviteService inviteService) {
        this.inviteService = inviteService;
    }

    @PostMapping("/accept-invite/{token}")
    public ApiResponse<AuthResponse> accept(@PathVariable String token, @Valid @RequestBody AcceptInviteRequest request) {
        return ApiResponse.ok(inviteService.accept(token, request));
    }
}
