package br.edu.waldorf.modules.security.api.controller;

import br.edu.waldorf.modules.security.api.dto.LoginRequestDTO;
import br.edu.waldorf.modules.security.api.dto.LoginResponseDTO;
import br.edu.waldorf.modules.security.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de login e tokens JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário e gerar tokens JWT")
    public ResponseEntity<LoginResponseDTO> login(@Validated @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // Endpoints de refresh/logout podem ser adicionados na próxima iteração
}
