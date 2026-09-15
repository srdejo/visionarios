## Context

`User` (`backend/modules/identity-access/.../User.java`) has no verified/enabled/status field today. `PasswordResetToken` (1h TTL, single-use, `expiresAt`/`used`) and `AdminInvite` (7-day TTL) are the two existing token-with-expiry entities and set the pattern to follow. Mail is not sent via SMTP: `MailSender`/`ContactApiMailSender` posts rendered HTML to an internal `contact` microservice; templates live in `mail-templates/*.html`. No `@Scheduled` job exists anywhere in the backend yet. Frontend auth forms (`register.ts`, `reset-password.ts`) use template-driven forms (`FormsModule` + `ngModel`), not reactive forms, and already share `app-password-input` (a `ControlValueAccessor`). See proposal.md - Why for motivation.

## Goals / Non-Goals

**Goals:**
- Reuse the existing token-entity pattern (`PasswordResetToken`) rather than inventing a new mechanism.
- Keep password-confirmation validation consistent with the project's existing DTO validation style (Jakarta bean validation on records).
- Make the 24h cleanup job idempotent and safe to run daily without impacting login/register traffic.

**Non-Goals:**
- Re-sending/resetting the verification token on request (no "resend verification email" endpoint) — out of scope for this change; can be a follow-up if users report missing the 24h window.
- Verification for admin-invite-created accounts — they already prove the email is real by receiving and clicking the invite link, so they are created pre-verified.
- Rate-limiting or CAPTCHA on registration — separate concern from confirming password and verifying email ownership.

## Decisions

- **New `verified` boolean + `EmailVerificationToken` entity, not a `status` enum.** A boolean matches the existing `emailOptOut` boolean precedent on `User` and keeps the login check a single field read. An entity mirroring `PasswordResetToken` (`id`, `token`, `userId`, `expiresAt`, `used`) is simpler than adding TTL bookkeeping onto `User` itself, and lets the cleanup job query tokens directly instead of scanning all users.
- **JWT is no longer issued at registration.** Alternative considered: issue the JWT immediately (as today) but block subsequent protected calls until verified. Rejected — the spec's whole point is to stop unverified accounts from being usable; handing out a session token at signup undermines that and would require every protected endpoint to special-case "authenticated but unverified," which is more surface area than checking `verified` once at login.
- **Cleanup runs once a day via `@Scheduled(cron = ...)` (e.g. at 03:00), deletes rows where `verified = false AND token.expiresAt < now`.** This is the first scheduled job in the backend, so `@EnableScheduling` must be added to the bootstrap config. A daily run (not exactly at the 24h mark) is simple, avoids per-account timers, and up to ~24h of slack past a given token's expiry is acceptable for this use case — an unverified account may live up to ~48h worst case before cleanup, which is still well within tolerance for reducing fake signups.
- **Password confirmation validated server-side via a manual equality check in `AuthService`, not a Jakarta `@AssertTrue` on the record.** `RegisterRequest`/`ResetPasswordRequest` are Java records; declarative cross-field validation on records is awkward (the `@AssertTrue` method must live in the record body). A one-line check at the top of `AuthService.register()`/`resetPassword()` (`if (!request.password().equals(request.confirmPassword())) throw new BusinessRuleException(...)`) matches how the service already throws for "email already registered" and is easier to read than a validation-annotation workaround.
- **Frontend match check is a plain component-level comparison (not a new Angular reactive-forms validator).** The codebase's auth forms are template-driven (`ngModel`), and there is no existing custom-validator utility (confirmed by research: zero hits for `passwordMatch`/`confirmPassword`). Introducing reactive-forms machinery for one field would be inconsistent with the rest of the auth module; a `password === confirmPassword` check inside `submitRegistration()`/the reset submit handler, surfaced as an inline error, matches existing conventions.
- **New public route `verificar-correo/:token`, modeled on `restablecer/:token`.** Same shape as the existing reset-password page: token from the URL, call a public endpoint, show success/failure state.

## Risks / Trade-offs

- [Deleting a user's account while they are mid-registration on a slow connection, right as the 24h boundary hits] → Acceptable: 24h is long enough that this is a rare edge case, and the user can simply register again.
- [Hourly cleanup job deletes rows that reference other tables via FK if any future capability starts attaching data to unverified users before verification] → Mitigate by deleting `EmailVerificationToken` and `User` in the same transaction, and by keeping this constraint documented in the spec (verification happens immediately after registration, before any other data attaches to the account).
- [`add-admin-user-directory-filters` is an in-flight change that also modifies the `Registro en wizard de 4 pasos` and `Registro exitoso` requirements in `user-auth` (adds `birthDate`)] → Flagged in proposal.md - Impact; whichever change archives second must re-diff against the already-archived spec and reconcile the requirement text (this change does not touch `birthDate`, so the merge is additive, not conflicting, but must be done by hand at archive time).
- [Blocking login entirely for unverified users, with no resend-email path] → Acceptable per explicit product decision (24h window, login blocked); if this proves too strict in practice, a resend endpoint is a small follow-up change against the same token entity.

## Migration Plan

- Backend: add `verified` column to `users` (default `false` for new rows; existing rows before this migration should default to `true` so current users are not locked out — a one-time backfill in the migration script).
- No rollback complexity beyond a standard schema migration revert; no destructive data changes to existing verified-by-default users.
