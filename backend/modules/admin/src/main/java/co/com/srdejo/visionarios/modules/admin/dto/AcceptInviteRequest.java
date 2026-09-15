package co.com.srdejo.visionarios.modules.admin.dto;

import co.com.srdejo.visionarios.modules.identityaccess.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AcceptInviteRequest(
        @NotBlank String fullName,
        @NotBlank String phone,
        @NotBlank @Size(min = 8) String password,
        @NotNull Profile profile
) {
}
