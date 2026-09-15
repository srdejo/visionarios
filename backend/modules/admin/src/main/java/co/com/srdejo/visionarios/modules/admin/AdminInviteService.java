package co.com.srdejo.visionarios.modules.admin;

import co.com.srdejo.visionarios.modules.admin.dto.AcceptInviteRequest;
import co.com.srdejo.visionarios.modules.admin.dto.AdminInviteResponse;
import co.com.srdejo.visionarios.modules.identityaccess.Role;
import co.com.srdejo.visionarios.modules.identityaccess.User;
import co.com.srdejo.visionarios.modules.identityaccess.UserRepository;
import co.com.srdejo.visionarios.modules.identityaccess.dto.AuthResponse;
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

import java.util.List;
import java.util.UUID;

@Service
public class AdminInviteService {

    private final AdminInviteRepository inviteRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailSender mailSender;
    private final String publicUrl;

    public AdminInviteService(AdminInviteRepository inviteRepository, UserRepository userRepository,
                               PasswordEncoder passwordEncoder, JwtService jwtService, MailSender mailSender,
                               @Value("${app.public-url}") String publicUrl) {
        this.inviteRepository = inviteRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mailSender = mailSender;
        this.publicUrl = publicUrl;
    }

    @Transactional
    public AdminInviteResponse create(String email, UUID invitedByUserId) {
        AdminInvite invite = new AdminInvite(UUID.randomUUID(), UUID.randomUUID().toString(), email);
        inviteRepository.save(invite);
        User inviter = userRepository.findById(invitedByUserId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        String link = publicUrl + "/aceptar-invitacion/" + invite.getToken();
        mailSender.sendAdminInvite(invite.getEmail(), inviter.getFullName(), link);
        return AdminInviteResponse.from(invite);
    }

    @Transactional(readOnly = true)
    public List<AdminInviteResponse> findAll() {
        return inviteRepository.findAllByOrderByCreatedAtDesc().stream().map(AdminInviteResponse::from).toList();
    }

    @Transactional
    public AuthResponse accept(String token, AcceptInviteRequest request) {
        AdminInvite invite = inviteRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Invitacion no encontrada"));
        if (invite.isUsed()) {
            throw new BusinessRuleException("Esta invitacion ya fue utilizada");
        }
        if (invite.isExpired()) {
            throw new BusinessRuleException("Esta invitacion vencio");
        }
        if (userRepository.existsByEmail(invite.getEmail())) {
            throw new BusinessRuleException("Ya existe una cuenta con ese correo");
        }
        User user = new User(UUID.randomUUID(), request.fullName(), request.phone(), invite.getEmail(),
                passwordEncoder.encode(request.password()), Role.ADMIN, request.profile());
        userRepository.save(user);
        invite.markUsed();
        String jwt = jwtService.issue(new JwtClaims(user.getId(), user.getRole().name()));
        return new AuthResponse(jwt, UserResponse.from(user));
    }
}
