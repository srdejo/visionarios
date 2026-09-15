package co.com.srdejo.visionarios.modules.identityaccess.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String confirmPassword
) {
}
