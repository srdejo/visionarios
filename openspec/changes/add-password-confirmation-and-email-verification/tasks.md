## 1. Data model

- [x] 1.1 Add `verified` boolean column to `User` (migration defaults existing rows to `true`, new rows to `false`) and verify the migration runs cleanly against a local/test database
- [x] 1.2 Create `EmailVerificationToken` entity (`id`, `token`, `userId`, `expiresAt` = created + 24h, `used`, `isExpired()`, `markUsed()`) mirroring `PasswordResetToken`, plus its repository, and verify with a unit test that `isExpired()`/`markUsed()` behave correctly

## 2. Backend: registration and password confirmation

- [x] 2.1 Add `confirmPassword` to `RegisterRequest` and `ResetPasswordRequest` DTOs and verify the field is present in request/response contract tests
- [x] 2.2 In `AuthService.register()`, reject when `password != confirmPassword` before any account creation, and verify with a unit test that no `User` row is created on mismatch
- [x] 2.3 In `AuthService.resetPassword()`, reject when `password != confirmPassword` before updating the stored hash, and verify with a unit test that the password is unchanged on mismatch
- [x] 2.4 Update `AuthService.register()` to create the `User` with `verified = false`, generate an `EmailVerificationToken`, and NOT issue a JWT in the response; verify with a unit test that the response contains no token and the created user is unverified

## 3. Backend: email verification and login gate

- [x] 3.1 Add `sendEmailVerification(toEmail, name, link)` to `MailSender`/`ContactApiMailSender` and a `mail-templates/verify-email.html` template (link to `{app.public-url}/verificar-correo/{token}`, explicit 24h deletion warning copy), and verify by rendering the template with sample data
- [x] 3.2 Wire `AuthService.register()` to send the verification email after creating the token, and verify via an integration test that registering triggers a call to the mail sender with the correct link
- [x] 3.3 Add `POST /api/auth/verify-email/{token}` (public, alongside other `/api/auth/**` routes) that marks the token used and the user verified, and verify with tests for valid, expired, used, and unknown tokens
- [x] 3.4 In `AuthService.login()`, after password check succeeds, reject with a "correo no verificado" error if `verified = false`, and verify with a unit test that a correct-password, unverified login is rejected without issuing a JWT
- [x] 3.5 Confirm the admin-invite acceptance path creates accounts with `verified = true` (no verification token issued), and verify with a test that invited admins can log in immediately

## 4. Backend: cleanup job

- [x] 4.1 Enable scheduling (`@EnableScheduling` on bootstrap config) and add a once-a-day `@Scheduled(cron = ...)` job (e.g. 03:00) that deletes `User` rows where `verified = false` and their `EmailVerificationToken.expiresAt` has passed, in one transaction per deleted account, and verify with a test seeding an expired-unverified user that the job removes both the user and its token
- [x] 4.2 Verify with a test seeding a not-yet-expired unverified user that the job leaves it untouched

## 5. Frontend: password confirmation

- [x] 5.1 Add a `confirmPassword` field (reusing `app-password-input`) to `register.html` step 1, and validate equality in `register.ts` before calling `submitRegistration()`, showing an inline error on mismatch; verify manually that submitting mismatched passwords blocks the request and shows the error
- [x] 5.2 Add a `confirmPassword` field to `reset-password.ts`/its template with the same equality check before calling `auth.resetPassword()`; verify manually that mismatched values block submission

## 6. Frontend: email verification flow

- [x] 6.1 Update the register wizard's step-4 confirmation copy to state the account must be verified via email before logging in, and that it will be deleted if not verified within 24 hours; verify by reviewing the rendered step 4
- [x] 6.2 Add route `verificar-correo/:token` and a page that calls the verify-email endpoint on load and shows success ("ya puedes iniciar sesión") or failure (expired/used/invalid) state; verify manually against a valid and an invalid token
- [x] 6.3 Handle the new "correo no verificado" login error in the login component with a clear message; verify manually by attempting login on an unverified test account

## 7. Cross-cutting verification

- [x] 7.1 Run backend test suite (`./gradlew test` for `identity-access` and affected modules) and confirm all pass
- [ ] 7.2 Manually walk the full flow end to end (register → receive/verify email link → login blocked before verification → login succeeds after) against a local run of frontend + backend
- [ ] 7.3 Manually verify an unverified test account is deleted after its token is force-expired and the cleanup job runs
