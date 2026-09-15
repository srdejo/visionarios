package co.com.srdejo.visionarios.modules.events;

import co.com.srdejo.visionarios.modules.events.dto.EventRequest;
import co.com.srdejo.visionarios.modules.events.dto.EventResponse;
import co.com.srdejo.visionarios.modules.identityaccess.CurrentUser;
import co.com.srdejo.visionarios.modules.identityaccess.Profile;
import co.com.srdejo.visionarios.modules.identityaccess.User;
import co.com.srdejo.visionarios.modules.identityaccess.UserRepository;
import co.com.srdejo.visionarios.platform.webcommon.NotFoundException;
import co.com.srdejo.visionarios.platform.webcommon.mail.EventInvitationMail;
import co.com.srdejo.visionarios.platform.webcommon.mail.MailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class EventService {

    private static final ZoneId CHURCH_ZONE = ZoneId.of("America/Bogota");
    private static final Locale ES = Locale.forLanguageTag("es-CO");

    private final EventRepository eventRepository;
    private final EventRsvpRepository rsvpRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;
    private final MailSender mailSender;
    private final String publicUrl;

    public EventService(EventRepository eventRepository, EventRsvpRepository rsvpRepository,
                         UserRepository userRepository, CurrentUser currentUser, MailSender mailSender,
                         @Value("${app.public-url}") String publicUrl) {
        this.eventRepository = eventRepository;
        this.rsvpRepository = rsvpRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
        this.mailSender = mailSender;
        this.publicUrl = publicUrl;
    }

    @Transactional(readOnly = true)
    public List<EventResponse> findAll() {
        UUID userId = currentUser.id();
        return eventRepository.findAllByOrderByStartsAtAsc().stream().map(event -> toResponse(event, userId)).toList();
    }

    @Transactional(readOnly = true)
    public EventResponse findById(UUID id) {
        Event event = getOrThrow(id);
        return toResponse(event, currentUser.id());
    }

    @Transactional
    public EventResponse toggleRsvp(UUID eventId) {
        getOrThrow(eventId);
        UUID userId = currentUser.id();
        rsvpRepository.findByEventIdAndUserId(eventId, userId).ifPresentOrElse(
                rsvpRepository::delete,
                () -> rsvpRepository.save(new EventRsvp(UUID.randomUUID(), eventId, userId)));
        return toResponse(getOrThrow(eventId), userId);
    }

    @Transactional
    public EventResponse create(EventRequest request) {
        Event event = new Event(UUID.randomUUID(), request.title(), request.startsAt(), request.place(),
                request.description(), request.targetProfile());
        eventRepository.save(event);
        return toResponse(event, currentUser.id());
    }

    @Transactional
    public EventResponse update(UUID id, EventRequest request) {
        Event event = getOrThrow(id);
        event.update(request.title(), request.startsAt(), request.place(), request.description(), request.targetProfile());
        return toResponse(event, currentUser.id());
    }

    /**
     * Envia el correo de invitacion (ver mail-templates/event-invitation.html) a los usuarios a
     * los que aplica el evento, salvo los que ya se dieron de baja (ver MailPreferencesService).
     */
    @Transactional(readOnly = true)
    public int notify(UUID id) {
        Event event = getOrThrow(id);
        List<User> recipients = (event.getTargetProfile() == null
                ? userRepository.findAll()
                : userRepository.findByProfile(event.getTargetProfile())).stream()
                .filter(user -> !user.isEmailOptOut())
                .toList();
        long confirmed = rsvpRepository.countByEventId(event.getId());
        for (User recipient : recipients) {
            mailSender.sendEventInvitation(recipient.getEmail(), toInvitationMail(event, recipient, confirmed));
        }
        return recipients.size();
    }

    private EventInvitationMail toInvitationMail(Event event, User recipient, long confirmed) {
        var startsAt = event.getStartsAt().atZone(CHURCH_ZONE);
        String dateTimeLine = startsAt.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", ES))
                + " · " + startsAt.format(DateTimeFormatter.ofPattern("h:mm a", ES)).toLowerCase(ES);
        String day = startsAt.format(DateTimeFormatter.ofPattern("d", ES));
        String month = startsAt.getMonth().getDisplayName(TextStyle.SHORT, ES);
        month = month.substring(0, 1).toUpperCase(ES) + month.substring(1).replace(".", "");
        String audienceLine = event.getTargetProfile() == null
                ? "Abierto a toda la red"
                : "Solo para " + profileLabel(event.getTargetProfile());
        String confirmedLine = confirmed == 1 ? "1 persona ya confirmó" : confirmed + " personas ya confirmaron";
        String eventLink = publicUrl + "/eventos/" + event.getId();
        String unsubscribeLink = publicUrl + "/darme-de-baja/" + recipient.getUnsubscribeToken();
        return new EventInvitationMail(recipient.getFullName(), event.getTitle(), dateTimeLine, event.getPlace(),
                event.getDescription(), day, month, audienceLine, confirmedLine, eventLink, eventLink, unsubscribeLink);
    }

    private static String profileLabel(Profile profile) {
        return switch (profile) {
            case PROFESIONAL -> "Profesionales";
            case EMPRENDEDOR -> "Emprendedores";
            case EMPRESARIO -> "Empresarios";
        };
    }

    private Event getOrThrow(UUID id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Evento no encontrado"));
    }

    private EventResponse toResponse(Event event, UUID userId) {
        long confirmed = rsvpRepository.countByEventId(event.getId());
        boolean byMe = rsvpRepository.findByEventIdAndUserId(event.getId(), userId).isPresent();
        return new EventResponse(event.getId(), event.getTitle(), event.getStartsAt(), event.getPlace(),
                event.getDescription(), event.getTargetProfile(), confirmed, byMe);
    }
}
