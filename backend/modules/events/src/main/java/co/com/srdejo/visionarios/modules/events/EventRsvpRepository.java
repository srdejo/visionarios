package co.com.srdejo.visionarios.modules.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EventRsvpRepository extends JpaRepository<EventRsvp, UUID> {

    Optional<EventRsvp> findByEventIdAndUserId(UUID eventId, UUID userId);

    long countByEventId(UUID eventId);

    Page<EventRsvp> findByEventIdOrderByConfirmedAtAsc(UUID eventId, Pageable pageable);
}
