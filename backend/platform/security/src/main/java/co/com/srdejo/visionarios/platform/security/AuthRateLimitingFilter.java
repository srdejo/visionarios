package co.com.srdejo.visionarios.platform.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Limite de intentos por IP para los endpoints publicos de auth (login,
 * registro, olvide-mi-clave), que no tienen otra proteccion contra fuerza
 * bruta / spam. Ventana fija en memoria; suficiente para un unico proceso
 * backend (ver infra/visionarios.service) — si corre mas de una instancia,
 * mover a un store compartido (ej. Redis).
 */
@Component
@Order(5)
public class AuthRateLimitingFilter extends OncePerRequestFilter {

    private record Rule(String path, int maxRequests, Duration window) {
    }

    private static final List<Rule> RULES = List.of(
            new Rule("/api/auth/login", 10, Duration.ofMinutes(1)),
            new Rule("/api/auth/register", 5, Duration.ofMinutes(10)),
            new Rule("/api/auth/forgot-password", 5, Duration.ofMinutes(10)),
            new Rule("/api/auth/resend-verification", 3, Duration.ofMinutes(10))
    );

    private static final class Counter {
        final AtomicInteger count = new AtomicInteger(0);
        volatile Instant windowStart = Instant.now();
    }

    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Rule rule = RULES.stream().filter(r -> r.path().equals(request.getRequestURI())).findFirst().orElse(null);
        if (rule != null && !allow(rule, clientIp(request))) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Demasiados intentos. Intenta de nuevo mas tarde.\"}");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean allow(Rule rule, String clientIp) {
        Counter counter = counters.computeIfAbsent(rule.path() + ":" + clientIp, key -> new Counter());
        synchronized (counter) {
            if (Duration.between(counter.windowStart, Instant.now()).compareTo(rule.window()) > 0) {
                counter.windowStart = Instant.now();
                counter.count.set(0);
            }
            return counter.count.incrementAndGet() <= rule.maxRequests();
        }
    }

    private static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Real-IP");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded;
        }
        return request.getRemoteAddr();
    }
}
