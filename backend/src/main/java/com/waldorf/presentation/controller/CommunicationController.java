package com.waldorf.presentation.controller;

import com.waldorf.application.dto.ComunicadoDTO;
import com.waldorf.application.dto.CreateComunicadoRequest;
import com.waldorf.application.service.ComunicadoService;
import com.waldorf.domain.entity.Usuario;
import com.waldorf.infrastructure.repository.UsuarioRepository;
import com.waldorf.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/communications")
@RequiredArgsConstructor
@Tag(name = "Communications", description = "Comunicados formais")
public class CommunicationController {

    private final ComunicadoService comunicadoService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    @Operation(summary = "Lista comunicados")
    public ResponseEntity<List<ComunicadoDTO>> listar() {
        return ResponseEntity.ok(comunicadoService.listar());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','PROFESSOR')")
    @Operation(summary = "Envia comunicado")
    public ResponseEntity<ComunicadoDTO> criar(
            @RequestBody CreateComunicadoRequest req,
            HttpServletRequest httpReq) {
        Long autorId = extrairUsuarioId(httpReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(comunicadoService.criar(req, autorId));
    }

    // FIX: JwtService não tem extractUserId() → usa extractUsername() (email) + busca por email
    private Long extrairUsuarioId(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        String token  = header != null && header.startsWith("Bearer ") ? header.substring(7) : "";
        String email  = jwtService.extractUsername(token);
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + email));
        return u.getId();
    }
}
