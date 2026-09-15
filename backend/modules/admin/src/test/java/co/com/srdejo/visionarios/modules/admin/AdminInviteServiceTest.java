package co.com.srdejo.visionarios.modules.admin;

import co.com.srdejo.visionarios.modules.admin.dto.AcceptInviteRequest;
import co.com.srdejo.visionarios.modules.identityaccess.Profile;
import co.com.srdejo.visionarios.modules.identityaccess.Role;
import co.com.srdejo.visionarios.modules.identityaccess.User;
import co.com.srdejo.visionarios.modules.identityaccess.UserRepository;
import co.com.srdejo.visionarios.platform.security.JwtClaims;
import co.com.srdejo.visionarios.platform.security.JwtService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminInviteServiceTest {

    @Mock
    private AdminInviteRepository inviteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private MailSender mailSender;

    private AdminInviteService adminInviteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminInviteService = new AdminInviteService(inviteRepository, userRepository, passwordEncoder, jwtService,
                mailSender, "https://visionarios.example.com");
    }

    @Test
    void acceptingInvite_createsAlreadyVerifiedAdmin_ableToLoginImmediately() {
        AdminInvite invite = new AdminInvite(UUID.randomUUID(), "tok123", "admin@example.com");
        when(inviteRepository.findByToken("tok123")).thenReturn(Optional.of(invite));
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(jwtService.issue(any(JwtClaims.class))).thenReturn("jwt-token");

        var response = adminInviteService.accept("tok123",
                new AcceptInviteRequest("Carlos Ruiz", "3000000000", "password123", Profile.PROFESIONAL));

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUser.capture());
        assertThat(savedUser.getValue().isVerified()).isTrue();
        assertThat(savedUser.getValue().getRole()).isEqualTo(Role.ADMIN);
        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(invite.isUsed()).isTrue();
    }
}
