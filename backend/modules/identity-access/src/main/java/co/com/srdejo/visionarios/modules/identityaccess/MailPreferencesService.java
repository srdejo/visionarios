package co.com.srdejo.visionarios.modules.identityaccess;

import co.com.srdejo.visionarios.platform.webcommon.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** "Darme de baja" de los correos de tipo boletín/invitación (ver mail-templates/event-invitation.html). */
@Service
public class MailPreferencesService {

    private final UserRepository userRepository;

    public MailPreferencesService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void unsubscribe(String token) {
        User user = userRepository.findByUnsubscribeToken(token)
                .orElseThrow(() -> new NotFoundException("Enlace de baja no encontrado"));
        user.optOutOfEmails();
    }
}
