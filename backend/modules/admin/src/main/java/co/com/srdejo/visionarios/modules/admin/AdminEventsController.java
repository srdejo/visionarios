package co.com.srdejo.visionarios.modules.admin;

import co.com.srdejo.visionarios.modules.identityaccess.dto.UserResponse;
import co.com.srdejo.visionarios.platform.webcommon.ApiResponse;
import co.com.srdejo.visionarios.platform.webcommon.PagedResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/events")
public class AdminEventsController {

    private final AdminEventsService adminEventsService;

    public AdminEventsController(AdminEventsService adminEventsService) {
        this.adminEventsService = adminEventsService;
    }

    @GetMapping("/{id}/attendees")
    public ApiResponse<PagedResponse<UserResponse>> attendees(@PathVariable UUID id,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(adminEventsService.attendees(id, page, size));
    }
}
