package co.com.srdejo.visionarios.platform.security;

import java.util.UUID;

public record JwtClaims(UUID userId, String role) {
}
