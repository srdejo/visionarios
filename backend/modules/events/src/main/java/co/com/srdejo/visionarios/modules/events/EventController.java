package co.com.srdejo.visionarios.modules.events;

import co.com.srdejo.visionarios.modules.events.dto.EventResponse;
import co.com.srdejo.visionarios.platform.webcommon.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ApiResponse<List<EventResponse>> findAll() {
        return ApiResponse.ok(eventService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<EventResponse> findById(@PathVariable UUID id) {
        return ApiResponse.ok(eventService.findById(id));
    }

    @PostMapping("/{id}/rsvp")
    public ApiResponse<EventResponse> rsvp(@PathVariable UUID id) {
        return ApiResponse.ok(eventService.toggleRsvp(id));
    }
}
