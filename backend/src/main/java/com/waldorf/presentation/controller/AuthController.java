package com.waldorf.presentation.controller;

import com.waldorf.application.dto.auth.LoginRequestDTO;
import com.waldorf.application.dto.auth.LoginResponseDTO;
import com.waldorf.application.dto.auth.RefreshRequestDTO;
import com.waldorf.application.service.AuthService;
import com.waldorf.infrastructure.security.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação")
public class AuthController {

    private static final String REFRESH_COOKIE = "refresh_token";

    private final AuthService authService;

    @Value("${app.cookie.secure:true}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site:Strict}")
    private String cookieSameSite;

    @Value("${jwt.expiration:86400000}")
    private long accessMaxAgeMillis;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshMaxAgeMillis;

    @PostMapping("/login")
    @Operation(summary = "Login com e-mail e senha")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO resp = authService.login(dto);
        return withAuthCookies(resp);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renova o access token usando refresh token")
    public ResponseEntity<LoginResponseDTO> refresh(@RequestBody(required = false) RefreshRequestDTO dto,
                                                    HttpServletRequest request) {
        String refreshToken = (dto != null && dto.refreshToken() != null)
                ? dto.refreshToken()
                : readCookie(request, REFRESH_COOKIE);
        LoginResponseDTO resp = authService.refresh(refreshToken);
        return withAuthCookies(resp);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout — revoga o token atual e limpa os cookies de sessão")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = resolveAccessToken(request);
        String email = null;
        authService.logout(token, email);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearCookie(JwtAuthenticationFilter.ACCESS_COOKIE).toString())
                .header(HttpHeaders.SET_COOKIE, clearCookie(REFRESH_COOKIE).toString())
                .build();
    }

    // ── Helpers de cookie ────────────────────────────────────────────────────

    private ResponseEntity<LoginResponseDTO> withAuthCookies(LoginResponseDTO resp) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        buildCookie(JwtAuthenticationFilter.ACCESS_COOKIE, resp.accessToken(), accessMaxAgeMillis).toString())
                .header(HttpHeaders.SET_COOKIE,
                        buildCookie(REFRESH_COOKIE, resp.refreshToken(), refreshMaxAgeMillis).toString())
                .body(resp); // corpo mantém os tokens para clientes Bearer (mobile)
    }

    private ResponseCookie buildCookie(String name, String value, long maxAgeMillis) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(Duration.ofMillis(maxAgeMillis))
                .build();
    }

    private ResponseCookie clearCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(0)
                .build();
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return readCookie(request, JwtAuthenticationFilter.ACCESS_COOKIE);
    }

    private String readCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
