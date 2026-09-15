package co.com.srdejo.visionarios.modules.identityaccess;

import co.com.srdejo.visionarios.platform.webcommon.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Publico (sin login): quien recibe el correo todavia no tiene sesion en el navegador donde hace clic. */
@RestController
@RequestMapping("/api/mail")
public class MailPreferencesController {

    private final MailPreferencesService mailPreferencesService;

    public MailPreferencesController(MailPreferencesService mailPreferencesService) {
        this.mailPreferencesService = mailPreferencesService;
    }

    @PostMapping("/unsubscribe/{token}")
    public ApiResponse<Void> unsubscribe(@PathVariable String token) {
        mailPreferencesService.unsubscribe(token);
        return ApiResponse.ok(null);
    }
}
