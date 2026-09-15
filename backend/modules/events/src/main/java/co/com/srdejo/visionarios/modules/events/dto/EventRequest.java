package co.com.srdejo.visionarios.modules.events.dto;

import co.com.srdejo.visionarios.modules.identityaccess.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record EventRequest(
        @NotBlank String title,
        @NotNull Instant startsAt,
        @NotBlank String place,
        @NotBlank String description,
        Profile targetProfile
) {
}
