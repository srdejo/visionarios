package co.com.srdejo.visionarios.modules.events;

import co.com.srdejo.visionarios.modules.events.dto.EventRequest;
import co.com.srdejo.visionarios.modules.events.dto.EventResponse;
import co.com.srdejo.visionarios.platform.webcommon.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/events")
public class AdminEventController {

    private final EventService eventService;

    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ApiResponse<EventResponse> create(@Valid @RequestBody EventRequest request) {
        return ApiResponse.ok(eventService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<EventResponse> update(@PathVariable UUID id, @Valid @RequestBody EventRequest request) {
        return ApiResponse.ok(eventService.update(id, request));
    }

    @PostMapping("/{id}/notify")
    public ApiResponse<Integer> notify(@PathVariable UUID id) {
        return ApiResponse.ok(eventService.notify(id));
    }
}
