package co.com.srdejo.visionarios.modules.identityaccess.dto;

import co.com.srdejo.visionarios.modules.identityaccess.BusinessCategory;
import co.com.srdejo.visionarios.modules.identityaccess.Profile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank String fullName,
        @NotBlank String phone,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, message = "La contraseña debe tener minimo 8 caracteres") String password,
        @NotBlank String confirmPassword,
        @NotNull Profile profile,
        LocalDate birthDate,
        // Profesional
        String profession,
        Integer yearsExperience,
        String currentCompany,
        // Emprendedor
        String businessProduct,
        String operatingTime,
        // Empresario
        String companyName,
        Integer employeeCount,
        Integer yearsWithCompany,
        // Emprendedor / Empresario
        BusinessCategory businessCategory
) implements ProfileFields {
}
