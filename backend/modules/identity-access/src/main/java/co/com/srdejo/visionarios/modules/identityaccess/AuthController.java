package co.com.srdejo.visionarios.modules.identityaccess;

import co.com.srdejo.visionarios.modules.identityaccess.dto.AuthResponse;
import co.com.srdejo.visionarios.modules.identityaccess.dto.ForgotPasswordRequest;
import co.com.srdejo.visionarios.modules.identityaccess.dto.LoginRequest;
import co.com.srdejo.visionarios.modules.identityaccess.dto.RegisterRequest;
import co.com.srdejo.visionarios.modules.identityaccess.dto.ResetPasswordRequest;
import co.com.srdejo.visionarios.platform.webcommon.ApiResponse;
import jakarta.validation.Valid;
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
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
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
        authService.resetPassword(request.token(), request.password());
        return ApiResponse.ok(null);
    }
}
