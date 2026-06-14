package com.waldorf.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Revogação de tokens JWT (blacklist) por jti, com TTL igual ao tempo restante de validade.
 *
 * <p>Usa Redis quando disponível. Em ambientes sem Redis (ex.: testes, onde a auto-configuração
 * do Redis é excluída), degrada para no-op de forma transparente — nenhum token é considerado
 * revogado, mantendo o comportamento stateless anterior.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String PREFIX = "jwt:blacklist:";

    private final ObjectProvider<StringRedisTemplate> redisProvider;

    /** Revoga o token identificado por {@code jti} até o instante de expiração informado. */
    public void revoke(String jti, long ttlMillis) {
        StringRedisTemplate redis = redisProvider.getIfAvailable();
        if (redis == null || jti == null) {
            log.debug("Blacklist indisponível (sem Redis); token não revogado server-side");
            return;
        }
        long ttl = Math.max(ttlMillis, 1_000);
        redis.opsForValue().set(PREFIX + jti, "revoked", Duration.ofMillis(ttl));
    }

    /** Indica se o token identificado por {@code jti} foi revogado. */
    public boolean isRevoked(String jti) {
        StringRedisTemplate redis = redisProvider.getIfAvailable();
        if (redis == null || jti == null) {
            return false;
        }
        return Boolean.TRUE.equals(redis.hasKey(PREFIX + jti));
    }
}
