package co.com.srdejo.visionarios.platform.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Denylist de JWT revocados (logout) en memoria. Suficiente para un unico
 * proceso backend (ver infra/visionarios.service); si en el futuro corre mas
 * de una instancia, esto debe moverse a un store compartido (ej. Redis).
 */
@Component
public class TokenRevocationStore {

    private final Map<String, Instant> revokedJtiExpiry = new ConcurrentHashMap<>();

    public void revoke(String jti, Instant tokenExpiresAt) {
        if (jti == null || tokenExpiresAt == null) {
            return;
        }
        revokedJtiExpiry.put(jti, tokenExpiresAt);
        revokedJtiExpiry.values().removeIf(expiry -> expiry.isBefore(Instant.now()));
    }

    public boolean isRevoked(String jti) {
        Instant expiresAt = revokedJtiExpiry.get(jti);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt.isBefore(Instant.now())) {
            revokedJtiExpiry.remove(jti);
            return false;
        }
        return true;
    }
}
