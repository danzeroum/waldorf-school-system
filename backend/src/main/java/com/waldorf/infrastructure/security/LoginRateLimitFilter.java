package com.waldorf.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiting simples (janela deslizante por IP) para o endpoint de login,
 * mitigando ataques de força bruta. Implementação puramente em memória (sem dependências
 * externas) — adequada para instância única; para múltiplas instâncias, migrar para Redis.
 */
@Slf4j
@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    @Value("${app.rate-limit.login.max-attempts:10}")
    private int maxAttempts;

    @Value("${app.rate-limit.login.window-seconds:60}")
    private long windowSeconds;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equalsIgnoreCase(request.getMethod())
                && request.getRequestURI().endsWith("/api/v1/auth/login"));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req,
                                    @NonNull HttpServletResponse res,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        String key = clientIp(req);
        long now = System.currentTimeMillis();
        long windowMillis = windowSeconds * 1_000;

        Window window = windows.compute(key, (k, w) -> {
            if (w == null || now - w.start > windowMillis) {
                return new Window(now);
            }
            return w;
        });

        if (window.count.incrementAndGet() > maxAttempts) {
            log.warn("Rate limit excedido no login para IP {}", key);
            res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(res.getWriter(), Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", HttpStatus.TOO_MANY_REQUESTS.value(),
                    "error", HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
                    "message", "Muitas tentativas de login. Tente novamente em instantes."));
            return;
        }
        chain.doFilter(req, res);
    }

    private String clientIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }

    private static final class Window {
        final long start;
        final AtomicInteger count = new AtomicInteger(0);
        Window(long start) { this.start = start; }
    }
}
