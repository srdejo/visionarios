package co.com.srdejo.visionarios.modules.events;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "event_rsvps")
public class EventRsvp {

    @Id
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "confirmed_at", nullable = false)
    private Instant confirmedAt;

    protected EventRsvp() {
    }

    public EventRsvp(UUID id, UUID eventId, UUID userId) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.confirmedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public UUID getUserId() {
        return userId;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }
}
