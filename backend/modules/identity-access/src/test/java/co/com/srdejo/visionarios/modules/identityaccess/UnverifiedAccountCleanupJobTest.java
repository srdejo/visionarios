package co.com.srdejo.visionarios.modules.identityaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UnverifiedAccountCleanupJobTest {

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Mock
    private UserRepository userRepository;

    private UnverifiedAccountCleanupJob job;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        job = new UnverifiedAccountCleanupJob(emailVerificationTokenRepository, userRepository, true);
    }

    @Test
    void deletesUserAndTokenWhenTokenExpiredAndUserStillUnverified() {
        UUID userId = UUID.randomUUID();
        EmailVerificationToken expiredToken = new EmailVerificationToken(UUID.randomUUID(), "tok", userId);
        User unverifiedUser = new User(userId, "Ana", "300", "ana@example.com", "hash", Role.USER, Profile.PROFESIONAL);
        unverifiedUser.markUnverified();

        when(emailVerificationTokenRepository.findByUsedFalseAndExpiresAtBefore(any(Instant.class)))
                .thenReturn(List.of(expiredToken));
        when(userRepository.findById(userId)).thenReturn(Optional.of(unverifiedUser));

        job.deleteExpiredUnverifiedAccounts();

        verify(userRepository, times(1)).delete(unverifiedUser);
    }

    @Test
    void leavesAlreadyVerifiedUserUntouched() {
        UUID userId = UUID.randomUUID();
        EmailVerificationToken expiredToken = new EmailVerificationToken(UUID.randomUUID(), "tok", userId);
        User verifiedUser = new User(userId, "Ana", "300", "ana@example.com", "hash", Role.USER, Profile.PROFESIONAL);

        when(emailVerificationTokenRepository.findByUsedFalseAndExpiresAtBefore(any(Instant.class)))
                .thenReturn(List.of(expiredToken));
        when(userRepository.findById(userId)).thenReturn(Optional.of(verifiedUser));

        job.deleteExpiredUnverifiedAccounts();

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void noExpiredTokens_deletesNothing() {
        when(emailVerificationTokenRepository.findByUsedFalseAndExpiresAtBefore(any(Instant.class)))
                .thenReturn(List.of());

        job.deleteExpiredUnverifiedAccounts();

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void withVerificationDisabled_neverDeletesAccounts() {
        UnverifiedAccountCleanupJob jobFlagOff =
                new UnverifiedAccountCleanupJob(emailVerificationTokenRepository, userRepository, false);

        jobFlagOff.deleteExpiredUnverifiedAccounts();

        verify(emailVerificationTokenRepository, never()).findByUsedFalseAndExpiresAtBefore(any(Instant.class));
        verify(userRepository, never()).delete(any(User.class));
    }
}
