package co.com.srdejo.visionarios.modules.admin;

import co.com.srdejo.visionarios.modules.admin.dto.AdminUsersResponse;
import co.com.srdejo.visionarios.modules.identityaccess.BusinessCategory;
import co.com.srdejo.visionarios.modules.identityaccess.Profile;
import co.com.srdejo.visionarios.platform.webcommon.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUsersController {

    private final AdminUsersService adminUsersService;

    public AdminUsersController(AdminUsersService adminUsersService) {
        this.adminUsersService = adminUsersService;
    }

    @GetMapping
    public ApiResponse<AdminUsersResponse> findAll(@RequestParam(required = false) String search,
                                                     @RequestParam(required = false) Profile profile,
                                                     @RequestParam(required = false) BusinessCategory businessCategory,
                                                     @RequestParam(required = false) Integer minAge,
                                                     @RequestParam(required = false) Integer maxAge,
                                                     @RequestParam(required = false) Integer minEmployees,
                                                     @RequestParam(required = false) Integer maxEmployees,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(adminUsersService.search(search, profile, businessCategory, minAge, maxAge,
                minEmployees, maxEmployees, page, size));
    }
}
