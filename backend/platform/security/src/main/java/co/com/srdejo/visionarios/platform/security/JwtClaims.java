package co.com.srdejo.visionarios.platform.security;

import java.time.Instant;
import java.util.UUID;

public record JwtClaims(UUID userId, String role, String jti, Instant expiresAt) {
}
