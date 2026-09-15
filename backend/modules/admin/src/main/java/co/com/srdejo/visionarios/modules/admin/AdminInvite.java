package co.com.srdejo.visionarios.modules.admin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "admin_invites")
public class AdminInvite {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String email;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AdminInvite() {
    }

    public AdminInvite(UUID id, String token, String email) {
        this.id = id;
        this.token = token;
        this.email = email;
        this.createdAt = Instant.now();
        this.expiresAt = this.createdAt.plusSeconds(7L * 24 * 3600);
        this.used = false;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public void markUsed() {
        this.used = true;
    }

    public UUID getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
