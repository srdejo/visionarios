package co.com.srdejo.visionarios.modules.admin;

import co.com.srdejo.visionarios.modules.admin.dto.AdminInviteRequest;
import co.com.srdejo.visionarios.modules.admin.dto.AdminInviteResponse;
import co.com.srdejo.visionarios.modules.identityaccess.CurrentUser;
import co.com.srdejo.visionarios.platform.webcommon.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/invites")
public class AdminInviteController {

    private final AdminInviteService inviteService;
    private final CurrentUser currentUser;

    public AdminInviteController(AdminInviteService inviteService, CurrentUser currentUser) {
        this.inviteService = inviteService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public ApiResponse<AdminInviteResponse> create(@Valid @RequestBody AdminInviteRequest request) {
        return ApiResponse.ok(inviteService.create(request.email(), currentUser.id()));
    }

    @GetMapping
    public ApiResponse<List<AdminInviteResponse>> findAll() {
        return ApiResponse.ok(inviteService.findAll());
    }
}
