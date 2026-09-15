package co.com.srdejo.visionarios.modules.identityaccess;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Elimina cuentas de registro normal que no confirmaron su correo dentro de las
 * 24 horas de vigencia de su {@link EmailVerificationToken} (ver Requirement:
 * Eliminación automática de cuentas no verificadas vencidas en openspec user-auth).
 */
@Component
public class UnverifiedAccountCleanupJob {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserRepository userRepository;

    public UnverifiedAccountCleanupJob(EmailVerificationTokenRepository emailVerificationTokenRepository,
                                        UserRepository userRepository) {
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void deleteExpiredUnverifiedAccounts() {
        List<EmailVerificationToken> expiredTokens = emailVerificationTokenRepository
                .findByUsedFalseAndExpiresAtBefore(Instant.now());
        for (EmailVerificationToken expiredToken : expiredTokens) {
            deleteIfStillUnverified(expiredToken);
        }
    }

    @Transactional
    void deleteIfStillUnverified(EmailVerificationToken expiredToken) {
        userRepository.findById(expiredToken.getUserId())
                .filter(user -> !user.isVerified())
                .ifPresent(userRepository::delete);
    }
}
