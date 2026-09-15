package co.com.srdejo.visionarios.modules.identityaccess.dto;

import co.com.srdejo.visionarios.modules.identityaccess.Profile;
import co.com.srdejo.visionarios.modules.identityaccess.Role;
import co.com.srdejo.visionarios.modules.identityaccess.User;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String phone,
        String email,
        Role role,
        Profile profile,
        String profession,
        Integer yearsExperience,
        String currentCompany,
        String businessProduct,
        String operatingTime,
        String companyName,
        Integer employeeCount,
        Integer yearsWithCompany,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(), user.getFullName(), user.getPhone(), user.getEmail(),
                user.getRole(), user.getProfile(),
                user.getProfession(), user.getYearsExperience(), user.getCurrentCompany(),
                user.getBusinessProduct(), user.getOperatingTime(),
                user.getCompanyName(), user.getEmployeeCount(), user.getYearsWithCompany(),
                user.getCreatedAt());
    }
}
