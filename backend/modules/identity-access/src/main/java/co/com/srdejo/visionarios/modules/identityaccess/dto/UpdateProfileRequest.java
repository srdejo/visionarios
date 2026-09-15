package co.com.srdejo.visionarios.modules.identityaccess.dto;

import co.com.srdejo.visionarios.modules.identityaccess.BusinessCategory;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UpdateProfileRequest(
        @NotBlank String fullName,
        @NotBlank String phone,
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
