package co.com.srdejo.visionarios.modules.materials;

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
@Table(name = "materials")
public class Material {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaterialType type;

    @Column(nullable = false)
    private String meta;

    @Column(name = "drive_url", nullable = false)
    private String driveUrl;

    /** Null significa visible para "toda la red". */
    @Enumerated(EnumType.STRING)
    @Column(name = "visible_profile")
    private Profile visibleProfile;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Material() {
    }

    public Material(UUID id, String title, MaterialType type, String meta, String driveUrl, Profile visibleProfile) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.meta = meta;
        this.driveUrl = driveUrl;
        this.visibleProfile = visibleProfile;
        this.createdAt = Instant.now();
    }

    public void update(String title, MaterialType type, String meta, String driveUrl, Profile visibleProfile) {
        this.title = title;
        this.type = type;
        this.meta = meta;
        this.driveUrl = driveUrl;
        this.visibleProfile = visibleProfile;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public MaterialType getType() {
        return type;
    }

    public String getMeta() {
        return meta;
    }

    public String getDriveUrl() {
        return driveUrl;
    }

    public Profile getVisibleProfile() {
        return visibleProfile;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
