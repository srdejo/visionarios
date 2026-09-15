package co.com.srdejo.visionarios.modules.identityaccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resuelve el huevo-y-gallina de "invitar administradores requiere ser administrador":
 * si {@code ADMIN_BOOTSTRAP_EMAIL} apunta a un usuario ya registrado (como USER, vía
 * /api/auth/register), lo promueve a ADMIN al arrancar. Idempotente — no hace nada si
 * ya es ADMIN o si el usuario todavia no se ha registrado (hay que registrarse primero,
 * esto solo hace el ultimo paso que hoy no se puede hacer desde la API).
 */
@Component
public class AdminBootstrapRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final UserRepository userRepository;
    private final String bootstrapEmail;

    public AdminBootstrapRunner(UserRepository userRepository,
                                 @Value("${app.admin-bootstrap-email:}") String bootstrapEmail) {
        this.userRepository = userRepository;
        this.bootstrapEmail = bootstrapEmail;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (bootstrapEmail == null || bootstrapEmail.isBlank()) {
            return;
        }
        userRepository.findByEmail(bootstrapEmail).ifPresentOrElse(user -> {
            if (user.getRole() == Role.ADMIN) {
                return;
            }
            user.promoteToAdmin();
            log.info("ADMIN_BOOTSTRAP_EMAIL: promovido a ADMIN el usuario {}", bootstrapEmail);
        }, () -> log.warn("ADMIN_BOOTSTRAP_EMAIL={} no tiene cuenta registrada todavia; regístrate primero en /registro", bootstrapEmail));
    }
}
