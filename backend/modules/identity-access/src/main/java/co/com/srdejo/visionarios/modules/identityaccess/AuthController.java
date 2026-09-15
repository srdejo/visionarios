package co.com.srdejo.visionarios.modules.identityaccess;

import co.com.srdejo.visionarios.modules.identityaccess.dto.AuthResponse;
import co.com.srdejo.visionarios.modules.identityaccess.dto.ForgotPasswordRequest;
import co.com.srdejo.visionarios.modules.identityaccess.dto.LoginRequest;
import co.com.srdejo.visionarios.modules.identityaccess.dto.RegisterRequest;
import co.com.srdejo.visionarios.modules.identityaccess.dto.RegisterResponse;
import co.com.srdejo.visionarios.modules.identityaccess.dto.ResetPasswordRequest;
import co.com.srdejo.visionarios.platform.security.JwtClaims;
import co.com.srdejo.visionarios.platform.webcommon.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.email());
        return ApiResponse.ok(null);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.token(), request.password(), request.confirmPassword());
        return ApiResponse.ok(null);
    }

    @PostMapping("/verify-email/{token}")
    public ApiResponse<Void> verifyEmail(@PathVariable String token) {
        authService.verifyEmail(token);
        return ApiResponse.ok(null);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal JwtClaims claims) {
        authService.logout(claims);
        return ApiResponse.ok(null);
    }
}
