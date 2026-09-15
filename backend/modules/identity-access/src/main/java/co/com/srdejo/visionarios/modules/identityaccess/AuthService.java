package co.com.srdejo.visionarios.modules.identityaccess;

import co.com.srdejo.visionarios.modules.identityaccess.dto.AuthResponse;
import co.com.srdejo.visionarios.modules.identityaccess.dto.LoginRequest;
import co.com.srdejo.visionarios.modules.identityaccess.dto.ProfileFields;
import co.com.srdejo.visionarios.modules.identityaccess.dto.RegisterRequest;
import co.com.srdejo.visionarios.modules.identityaccess.dto.RegisterResponse;
import co.com.srdejo.visionarios.modules.identityaccess.dto.UpdateProfileRequest;
import co.com.srdejo.visionarios.modules.identityaccess.dto.UserResponse;
import co.com.srdejo.visionarios.platform.security.JwtClaims;
import co.com.srdejo.visionarios.platform.security.JwtService;
import co.com.srdejo.visionarios.platform.security.TokenRevocationStore;
import co.com.srdejo.visionarios.platform.webcommon.BusinessRuleException;
import co.com.srdejo.visionarios.platform.webcommon.NotFoundException;
import co.com.srdejo.visionarios.platform.webcommon.mail.MailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRevocationStore tokenRevocationStore;
    private final MailSender mailSender;
    private final String publicUrl;

    public AuthService(UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository,
                        EmailVerificationTokenRepository emailVerificationTokenRepository,
                        PasswordEncoder passwordEncoder, JwtService jwtService, TokenRevocationStore tokenRevocationStore,
                        MailSender mailSender, @Value("${app.public-url}") String publicUrl) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRevocationStore = tokenRevocationStore;
        this.mailSender = mailSender;
        this.publicUrl = publicUrl;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new BusinessRuleException("Las contraseñas no coinciden");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Ya existe una cuenta con ese correo");
        }
        User user = new User(UUID.randomUUID(), request.fullName(), request.phone(), request.email(),
                passwordEncoder.encode(request.password()), Role.USER, request.profile());
        user.setBirthDate(request.birthDate());
        applyProfileFields(user, request.profile(), request);
        user.markUnverified();
        userRepository.save(user);

        EmailVerificationToken verificationToken = new EmailVerificationToken(UUID.randomUUID(), UUID.randomUUID().toString(), user.getId());
        emailVerificationTokenRepository.save(verificationToken);
        String link = publicUrl + "/verificar-correo/" + verificationToken.getToken();
        mailSender.sendEmailVerification(user.getEmail(), user.getFullName(), link);

        return new RegisterResponse(UserResponse.from(user),
                "Revisa tu correo para confirmar tu cuenta. Tienes 24 horas antes de que se elimine.");
    }

    private void applyProfileFields(User user, Profile profile, ProfileFields fields) {
        switch (profile) {
            case PROFESIONAL -> {
                user.setProfession(fields.profession());
                user.setYearsExperience(fields.yearsExperience());
                user.setCurrentCompany(fields.currentCompany());
            }
            case EMPRENDEDOR -> {
                user.setBusinessProduct(fields.businessProduct());
                user.setOperatingTime(fields.operatingTime());
                user.setBusinessCategory(fields.businessCategory());
            }
            case EMPRESARIO -> {
                user.setCompanyName(fields.companyName());
                user.setEmployeeCount(fields.employeeCount());
                user.setYearsWithCompany(fields.yearsWithCompany());
                user.setBusinessCategory(fields.businessCategory());
            }
        }
    }

    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        user.setFullName(request.fullName());
        user.setPhone(request.phone());
        user.setBirthDate(request.birthDate());
        applyProfileFields(user, user.getProfile(), request);
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessRuleException("Correo o contraseña invalidos"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessRuleException("Correo o contraseña invalidos");
        }
        if (!user.isVerified()) {
            throw new BusinessRuleException("Debes verificar tu correo antes de iniciar sesión");
        }
        return new AuthResponse(issueToken(user), UserResponse.from(user));
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("No existe una cuenta con ese correo"));
        PasswordResetToken resetToken = new PasswordResetToken(UUID.randomUUID(), UUID.randomUUID().toString(), user.getId());
        passwordResetTokenRepository.save(resetToken);
        String link = publicUrl + "/restablecer/" + resetToken.getToken();
        mailSender.sendPasswordReset(user.getEmail(), user.getFullName(), link);
    }

    @Transactional
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessRuleException("Las contraseñas no coinciden");
        }
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Enlace de restablecimiento no encontrado"));
        if (resetToken.isUsed()) {
            throw new BusinessRuleException("Este enlace ya fue utilizado");
        }
        if (resetToken.isExpired()) {
            throw new BusinessRuleException("Este enlace vencio");
        }
        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        user.changePassword(passwordEncoder.encode(newPassword));
        resetToken.markUsed();
    }

    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Enlace de verificación no encontrado"));
        if (verificationToken.isUsed()) {
            throw new BusinessRuleException("Este enlace ya fue utilizado");
        }
        if (verificationToken.isExpired()) {
            throw new BusinessRuleException("Este enlace vencio");
        }
        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        user.markVerified();
        verificationToken.markUsed();
    }

    public String issueToken(User user) {
        return jwtService.issue(new JwtClaims(user.getId(), user.getRole().name(), null, null));
    }

    public void logout(JwtClaims claims) {
        tokenRevocationStore.revoke(claims.jti(), claims.expiresAt());
    }
}
