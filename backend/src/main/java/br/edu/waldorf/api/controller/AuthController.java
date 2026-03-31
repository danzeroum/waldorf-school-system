package br.edu.waldorf.api.controller;

import com.waldorf.application.dto.auth.LoginRequestDTO;
import com.waldorf.application.dto.auth.LoginResponseDTO;
import com.waldorf.application.dto.auth.RefreshRequestDTO;
import com.waldorf.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login, refresh e logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login com e-mail e senha")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renova o access token usando refresh token")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody RefreshRequestDTO dto) {
        return ResponseEntity.ok(authService.refresh(dto.refreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Invalida o token do usuário autenticado")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String bearerToken,
            Authentication authentication) {
        String token = bearerToken.replace("Bearer ", "").trim();
        authService.logout(token, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
