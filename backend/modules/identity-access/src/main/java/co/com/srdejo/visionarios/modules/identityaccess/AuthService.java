package co.com.srdejo.visionarios.modules.identityaccess;

import co.com.srdejo.visionarios.modules.identityaccess.dto.AuthResponse;
import co.com.srdejo.visionarios.modules.identityaccess.dto.LoginRequest;
import co.com.srdejo.visionarios.modules.identityaccess.dto.RegisterRequest;
import co.com.srdejo.visionarios.modules.identityaccess.dto.UserResponse;
import co.com.srdejo.visionarios.platform.security.JwtClaims;
import co.com.srdejo.visionarios.platform.security.JwtService;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailSender mailSender;
    private final String publicUrl;

    public AuthService(UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository,
                        PasswordEncoder passwordEncoder, JwtService jwtService, MailSender mailSender,
                        @Value("${app.public-url}") String publicUrl) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mailSender = mailSender;
        this.publicUrl = publicUrl;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Ya existe una cuenta con ese correo");
        }
        User user = new User(UUID.randomUUID(), request.fullName(), request.phone(), request.email(),
                passwordEncoder.encode(request.password()), Role.USER, request.profile());
        applyProfileFields(user, request);
        userRepository.save(user);
        return new AuthResponse(issueToken(user), UserResponse.from(user));
    }

    private void applyProfileFields(User user, RegisterRequest request) {
        switch (request.profile()) {
            case PROFESIONAL -> {
                user.setProfession(request.profession());
                user.setYearsExperience(request.yearsExperience());
                user.setCurrentCompany(request.currentCompany());
            }
            case EMPRENDEDOR -> {
                user.setBusinessProduct(request.businessProduct());
                user.setOperatingTime(request.operatingTime());
            }
            case EMPRESARIO -> {
                user.setCompanyName(request.companyName());
                user.setEmployeeCount(request.employeeCount());
                user.setYearsWithCompany(request.yearsWithCompany());
            }
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessRuleException("Correo o contraseña invalidos"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessRuleException("Correo o contraseña invalidos");
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
    public void resetPassword(String token, String newPassword) {
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

    public String issueToken(User user) {
        return jwtService.issue(new JwtClaims(user.getId(), user.getRole().name()));
    }
}
