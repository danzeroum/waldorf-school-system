package br.edu.waldorf.infrastructure.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Blacklist de tokens JWT invalidados via logout.
 * Implementação in-memory — substitua por Redis em produção.
 */
@Component
public class TokenBlacklist {

    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void invalidate(String token, long expirationMs) {
        blacklist.put(token, expirationMs);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }

    /** Remove tokens já expirados a cada 10 minutos para evitar vazamento de memória. */
    @Scheduled(fixedDelay = 600_000)
    public void evictExpiredTokens() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }
}
