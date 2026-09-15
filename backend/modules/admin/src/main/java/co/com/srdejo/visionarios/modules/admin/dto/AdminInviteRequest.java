package co.com.srdejo.visionarios.modules.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminInviteRequest(@NotBlank @Email String email) {
}
