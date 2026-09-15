package co.com.srdejo.visionarios.modules.materials.dto;

import co.com.srdejo.visionarios.modules.identityaccess.Profile;
import co.com.srdejo.visionarios.modules.materials.MaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MaterialRequest(
        @NotBlank String title,
        @NotNull MaterialType type,
        @NotBlank String meta,
        @NotBlank String driveUrl,
        Profile visibleProfile
) {
}
