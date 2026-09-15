package co.com.srdejo.visionarios.modules.admin;

import co.com.srdejo.visionarios.modules.events.EventRepository;
import co.com.srdejo.visionarios.modules.events.EventRsvp;
import co.com.srdejo.visionarios.modules.events.EventRsvpRepository;
import co.com.srdejo.visionarios.modules.identityaccess.User;
import co.com.srdejo.visionarios.modules.identityaccess.UserRepository;
import co.com.srdejo.visionarios.modules.identityaccess.dto.UserResponse;
import co.com.srdejo.visionarios.platform.webcommon.NotFoundException;
import co.com.srdejo.visionarios.platform.webcommon.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminEventsService {

    private final EventRepository eventRepository;
    private final EventRsvpRepository rsvpRepository;
    private final UserRepository userRepository;

    public AdminEventsService(EventRepository eventRepository, EventRsvpRepository rsvpRepository,
                               UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.rsvpRepository = rsvpRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> attendees(UUID eventId, int page, int size) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Evento no encontrado");
        }
        Page<EventRsvp> rsvps = rsvpRepository.findByEventIdOrderByConfirmedAtAsc(eventId, PageRequest.of(page, size));
        List<UUID> userIds = rsvps.getContent().stream().map(EventRsvp::getUserId).toList();
        Map<UUID, User> usersById = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        List<UserResponse> content = rsvps.getContent().stream()
                .map(rsvp -> usersById.get(rsvp.getUserId()))
                .filter(user -> user != null)
                .map(UserResponse::from)
                .toList();
        return new PagedResponse<>(content, rsvps.getNumber(), rsvps.getSize(),
                rsvps.getTotalElements(), rsvps.getTotalPages());
    }
}
