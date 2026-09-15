package co.com.srdejo.visionarios.modules.events;

import co.com.srdejo.visionarios.modules.identityaccess.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(nullable = false)
    private String place;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    /** Null significa "toda la red" (visible para todos los perfiles). */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_profile")
    private Profile targetProfile;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Event() {
    }

    public Event(UUID id, String title, Instant startsAt, String place, String description, Profile targetProfile) {
        this.id = id;
        this.title = title;
        this.startsAt = startsAt;
        this.place = place;
        this.description = description;
        this.targetProfile = targetProfile;
        this.createdAt = Instant.now();
    }

    public void update(String title, Instant startsAt, String place, String description, Profile targetProfile) {
        this.title = title;
        this.startsAt = startsAt;
        this.place = place;
        this.description = description;
        this.targetProfile = targetProfile;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Instant getStartsAt() {
        return startsAt;
    }

    public String getPlace() {
        return place;
    }

    public String getDescription() {
        return description;
    }

    public Profile getTargetProfile() {
        return targetProfile;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
