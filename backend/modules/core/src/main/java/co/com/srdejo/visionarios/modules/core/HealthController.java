package co.com.srdejo.visionarios.modules.core;

import co.com.srdejo.visionarios.platform.webcommon.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Placeholder de modulo de dominio: "visionarios" todavia no tiene funcionalidad de negocio
 * definida (ver docs/DECISIONS.md). Este endpoint solo confirma que backend, base de datos
 * (via Flyway) y frontend estan conectados.
 */
@RestController
public class HealthController {

    @GetMapping("/api/ping")
    public ApiResponse<Map<String, Object>> ping() {
        return ApiResponse.ok(Map.of(
                "status", "ok",
                "timestamp", Instant.now().toString()
        ));
    }
}
