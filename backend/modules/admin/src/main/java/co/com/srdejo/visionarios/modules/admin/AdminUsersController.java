package co.com.srdejo.visionarios.modules.admin;

import co.com.srdejo.visionarios.modules.admin.dto.AdminUsersResponse;
import co.com.srdejo.visionarios.platform.webcommon.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUsersController {

    private final AdminUsersService adminUsersService;

    public AdminUsersController(AdminUsersService adminUsersService) {
        this.adminUsersService = adminUsersService;
    }

    @GetMapping
    public ApiResponse<AdminUsersResponse> findAll() {
        return ApiResponse.ok(adminUsersService.findAll());
    }
}
