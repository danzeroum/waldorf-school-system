package com.waldorf.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String gerarToken(UserDetails user) {
        return buildToken(new HashMap<>(), user, expiration);
    }

    public String gerarRefreshToken(UserDetails user) {
        return buildToken(new HashMap<>(), user, refreshExpiration);
    }

    private String buildToken(Map<String, Object> claims, UserDetails user, long exp) {
        return Jwts.builder()
                .claims(claims)
                .id(UUID.randomUUID().toString())
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + exp))
                .signWith(getKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Identificador único do token (claim {@code jti}), usado para revogação/blacklist. */
    public String extractJti(String token) {
        return extractClaim(token, Claims::getId);
    }

    /** Instante de expiração do token. */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /** Milissegundos restantes até a expiração do token (mínimo 0). */
    public long remainingMillis(String token) {
        return Math.max(0, extractExpiration(token).getTime() - System.currentTimeMillis());
    }

    public boolean isTokenValid(String token, UserDetails user) {
        return extractUsername(token).equals(user.getUsername()) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> fn) {
        Claims claims = Jwts.parser().verifyWith(getKey()).build()
                .parseSignedClaims(token).getPayload();
        return fn.apply(claims);
    }
}
