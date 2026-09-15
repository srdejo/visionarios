package co.com.srdejo.visionarios.modules.identityaccess.dto;

import co.com.srdejo.visionarios.modules.identityaccess.BusinessCategory;
import co.com.srdejo.visionarios.modules.identityaccess.Profile;
import co.com.srdejo.visionarios.modules.identityaccess.Role;
import co.com.srdejo.visionarios.modules.identityaccess.User;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String phone,
        String email,
        Role role,
        Profile profile,
        LocalDate birthDate,
        String profession,
        Integer yearsExperience,
        String currentCompany,
        String businessProduct,
        String operatingTime,
        String companyName,
        Integer employeeCount,
        Integer yearsWithCompany,
        BusinessCategory businessCategory,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(), user.getFullName(), user.getPhone(), user.getEmail(),
                user.getRole(), user.getProfile(), user.getBirthDate(),
                user.getProfession(), user.getYearsExperience(), user.getCurrentCompany(),
                user.getBusinessProduct(), user.getOperatingTime(),
                user.getCompanyName(), user.getEmployeeCount(), user.getYearsWithCompany(),
                user.getBusinessCategory(),
                user.getCreatedAt());
    }
}
