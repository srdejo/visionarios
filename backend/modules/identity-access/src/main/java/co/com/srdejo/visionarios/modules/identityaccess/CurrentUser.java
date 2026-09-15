package co.com.srdejo.visionarios.modules.identityaccess;

import co.com.srdejo.visionarios.platform.security.JwtClaims;
import co.com.srdejo.visionarios.platform.webcommon.NotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Resolves the authenticated user's id from the JWT-backed security context. */
@Component
public class CurrentUser {

    public UUID id() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtClaims claims)) {
            throw new NotFoundException("No hay usuario autenticado en el contexto");
        }
        return claims.userId();
    }
}
