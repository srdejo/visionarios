package co.com.srdejo.visionarios.modules.identityaccess;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Profile profile;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_category")
    private BusinessCategory businessCategory;

    // Profesional
    @Column(name = "profession")
    private String profession;
    @Column(name = "years_experience")
    private Integer yearsExperience;
    @Column(name = "current_company")
    private String currentCompany;

    // Emprendedor
    @Column(name = "business_product")
    private String businessProduct;
    @Column(name = "operating_time")
    private String operatingTime;

    // Empresario
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "employee_count")
    private Integer employeeCount;
    @Column(name = "years_with_company")
    private Integer yearsWithCompany;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "email_opt_out", nullable = false)
    private boolean emailOptOut;

    @Column(name = "unsubscribe_token", nullable = false, unique = true)
    private String unsubscribeToken;

    @Column(nullable = false)
    private boolean verified;

    protected User() {
    }

    public User(UUID id, String fullName, String phone, String email, String passwordHash, Role role, Profile profile) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.profile = profile;
        this.createdAt = Instant.now();
        this.emailOptOut = false;
        this.unsubscribeToken = UUID.randomUUID().toString();
        this.verified = true;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public Profile getProfile() {
        return profile;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public BusinessCategory getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(BusinessCategory businessCategory) {
        this.businessCategory = businessCategory;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public String getCurrentCompany() {
        return currentCompany;
    }

    public void setCurrentCompany(String currentCompany) {
        this.currentCompany = currentCompany;
    }

    public String getBusinessProduct() {
        return businessProduct;
    }

    public void setBusinessProduct(String businessProduct) {
        this.businessProduct = businessProduct;
    }

    public String getOperatingTime() {
        return operatingTime;
    }

    public void setOperatingTime(String operatingTime) {
        this.operatingTime = operatingTime;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }

    public Integer getYearsWithCompany() {
        return yearsWithCompany;
    }

    public void setYearsWithCompany(Integer yearsWithCompany) {
        this.yearsWithCompany = yearsWithCompany;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void promoteToAdmin() {
        this.role = Role.ADMIN;
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public boolean isEmailOptOut() {
        return emailOptOut;
    }

    public String getUnsubscribeToken() {
        return unsubscribeToken;
    }

    public void optOutOfEmails() {
        this.emailOptOut = true;
    }

    public boolean isVerified() {
        return verified;
    }

    public void markUnverified() {
        this.verified = false;
    }

    public void markVerified() {
        this.verified = true;
    }
}
