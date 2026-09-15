package co.com.srdejo.visionarios.modules.identityaccess;

import co.com.srdejo.visionarios.modules.identityaccess.dto.LoginRequest;
import co.com.srdejo.visionarios.modules.identityaccess.dto.RegisterRequest;
import co.com.srdejo.visionarios.modules.identityaccess.dto.RegisterResponse;
import co.com.srdejo.visionarios.platform.security.JwtService;
import co.com.srdejo.visionarios.platform.webcommon.BusinessRuleException;
import co.com.srdejo.visionarios.platform.webcommon.NotFoundException;
import co.com.srdejo.visionarios.platform.webcommon.mail.MailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private MailSender mailSender;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userRepository, passwordResetTokenRepository, emailVerificationTokenRepository,
                passwordEncoder, jwtService, mailSender, "https://visionarios.example.com");
    }

    private RegisterRequest registerRequest(String password, String confirmPassword) {
        return new RegisterRequest("Ana Torres", "3000000000", "ana@example.com", password, confirmPassword,
                Profile.PROFESIONAL, null, null, null, null, null, null, null, null, null, null);
    }

    @Test
    void register_rejectsMismatchedPasswords_andCreatesNoUser() {
        RegisterRequest request = registerRequest("password123", "different123");

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Las contraseñas no coinciden");

        verify(userRepository, never()).save(any());
        verify(mailSender, never()).sendEmailVerification(anyString(), anyString(), anyString());
    }

    @Test
    void register_createsUnverifiedUser_sendsVerificationEmail_andReturnsNoToken() {
        RegisterRequest request = registerRequest("password123", "password123");
        when(userRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");

        RegisterResponse response = authService.register(request);

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUser.capture());
        assertThat(savedUser.getValue().isVerified()).isFalse();

        verify(emailVerificationTokenRepository).save(any(EmailVerificationToken.class));
        verify(mailSender).sendEmailVerification(eq("ana@example.com"), eq("Ana Torres"), anyString());
        verify(jwtService, never()).issue(any());

        assertThat(response.user().email()).isEqualTo("ana@example.com");
        assertThat(response.message()).isNotBlank();
    }

    @Test
    void register_rejectsAlreadyRegisteredEmail() {
        RegisterRequest request = registerRequest("password123", "password123");
        when(userRepository.existsByEmail("ana@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Ya existe una cuenta con ese correo");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_rejectsUnverifiedAccount_withoutIssuingToken() {
        User user = new User(UUID.randomUUID(), "Ana Torres", "3000000000", "ana@example.com", "hashed",
                Role.USER, Profile.PROFESIONAL);
        user.markUnverified();
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(new LoginRequest("ana@example.com", "password123")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Debes verificar tu correo antes de iniciar sesión");

        verify(jwtService, never()).issue(any());
    }

    @Test
    void login_verifiedAccount_issuesToken() {
        User user = new User(UUID.randomUUID(), "Ana Torres", "3000000000", "ana@example.com", "hashed",
                Role.USER, Profile.PROFESIONAL);
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtService.issue(any())).thenReturn("jwt-token");

        var response = authService.login(new LoginRequest("ana@example.com", "password123"));

        assertThat(response.token()).isEqualTo("jwt-token");
    }

    @Test
    void resetPassword_rejectsMismatchedConfirmation_andDoesNotChangePassword() {
        assertThatThrownBy(() -> authService.resetPassword("token", "newPassword1", "different1"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Las contraseñas no coinciden");

        verify(passwordResetTokenRepository, never()).findByToken(anyString());
    }

    @Test
    void verifyEmail_marksTokenUsed_andUserVerified() {
        UUID userId = UUID.randomUUID();
        EmailVerificationToken token = new EmailVerificationToken(UUID.randomUUID(), "tok123", userId);
        User user = new User(userId, "Ana Torres", "3000000000", "ana@example.com", "hashed",
                Role.USER, Profile.PROFESIONAL);
        user.markUnverified();
        when(emailVerificationTokenRepository.findByToken("tok123")).thenReturn(Optional.of(token));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        authService.verifyEmail("tok123");

        assertThat(user.isVerified()).isTrue();
        assertThat(token.isUsed()).isTrue();
    }

    @Test
    void verifyEmail_rejectsExpiredToken() {
        UUID userId = UUID.randomUUID();
        EmailVerificationToken token = new EmailVerificationToken(UUID.randomUUID(), "tok123", userId);
        forceExpire(token);
        when(emailVerificationTokenRepository.findByToken("tok123")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.verifyEmail("tok123"))
                .isInstanceOf(BusinessRuleException.class);

        verify(userRepository, never()).findById(any());
    }

    @Test
    void verifyEmail_rejectsUnknownToken() {
        when(emailVerificationTokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.verifyEmail("missing"))
                .isInstanceOf(NotFoundException.class);
    }

    private static void forceExpire(EmailVerificationToken token) {
        try {
            var field = EmailVerificationToken.class.getDeclaredField("expiresAt");
            field.setAccessible(true);
            field.set(token, java.time.Instant.now().minusSeconds(60));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
