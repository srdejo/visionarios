package co.com.srdejo.visionarios.modules.events.dto;

import co.com.srdejo.visionarios.modules.identityaccess.Profile;

import java.time.Instant;
import java.util.UUID;

public record EventResponse(
        UUID id,
        String title,
        Instant startsAt,
        String place,
        String description,
        Profile targetProfile,
        long confirmedCount,
        boolean confirmedByMe
) {
}
