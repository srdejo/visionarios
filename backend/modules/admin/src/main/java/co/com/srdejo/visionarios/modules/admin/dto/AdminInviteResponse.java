package co.com.srdejo.visionarios.modules.admin.dto;

import co.com.srdejo.visionarios.modules.admin.AdminInvite;

import java.time.Instant;
import java.util.UUID;

public record AdminInviteResponse(UUID id, String token, String email, Instant expiresAt, boolean used, boolean expired) {
    public static AdminInviteResponse from(AdminInvite invite) {
        return new AdminInviteResponse(invite.getId(), invite.getToken(), invite.getEmail(), invite.getExpiresAt(),
                invite.isUsed(), invite.isExpired());
    }
}
