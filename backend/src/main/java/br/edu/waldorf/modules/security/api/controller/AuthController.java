package br.edu.waldorf.modules.security.api.controller;

import br.edu.waldorf.core.auth.UserPrincipal;
import br.edu.waldorf.modules.security.api.dto.*;
import br.edu.waldorf.modules.security.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST de Autenticação — JWT stateless.
 * Cobre login, refresh, logout, perfil e verificação de permissão contextual.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de autenticação e autorização JWT")
public class AuthController {

    private final AuthService authService;

    // ---------------------------------------------------------------
    // POST /api/v1/auth/login
    // ---------------------------------------------------------------
    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário e gerar tokens JWT")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ---------------------------------------------------------------
    // POST /api/v1/auth/refresh
    // ---------------------------------------------------------------
    @PostMapping("/refresh")
    @Operation(summary = "Renovar access token usando refresh token")
    public ResponseEntity<LoginResponseDTO> refresh(
            @Valid @RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    // ---------------------------------------------------------------
    // POST /api/v1/auth/logout
    // ---------------------------------------------------------------
    @PostMapping("/logout")
    @Operation(summary = "Revogar tokens e encerrar sessão",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        String authHeader = request.getHeader("Authorization");
        String token = (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7) : null;
        authService.logout(user.getId(), token);
        return ResponseEntity.noContent().build();
    }

    // ---------------------------------------------------------------
    // GET /api/v1/auth/me
    // ---------------------------------------------------------------
    @GetMapping("/me")
    @Operation(summary = "Retorna perfil do usuário autenticado",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MeResponseDTO> me(
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(authService.getMe(user));
    }

    // ---------------------------------------------------------------
    // POST /api/v1/auth/check-permission
    // ---------------------------------------------------------------
    @PostMapping("/check-permission")
    @Operation(summary = "Verificar se o usuário possui permissão contextual",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CheckPermissionResponseDTO> checkPermission(
            @Valid @RequestBody CheckPermissionRequestDTO request,
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(authService.checkPermission(request, user));
    }
}
