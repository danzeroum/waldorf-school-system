package com.waldorf.presentation.controller;

import com.waldorf.application.dto.AvisoDTO;
import com.waldorf.application.dto.CreateAvisoRequest;
import com.waldorf.application.service.AvisoService;
import com.waldorf.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
@Tag(name = "Announcements", description = "Avisos e comunicados do mural")
public class AnnouncementController {

    private final AvisoService avisoService;
    private final JwtService jwtService;

    @GetMapping
    @Operation(summary = "Lista avisos")
    public ResponseEntity<List<AvisoDTO>> listar(
            @RequestParam(required = false) Long turmaId) {
        return ResponseEntity.ok(avisoService.listar(turmaId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','PROFESSOR')")
    @Operation(summary = "Cria aviso")
    public ResponseEntity<AvisoDTO> criar(
            @RequestBody CreateAvisoRequest req,
            HttpServletRequest httpReq) {
        Long autorId = extrairUsuarioId(httpReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(avisoService.criar(req, autorId));
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "Marca aviso como lido (registro de leitura)")
    public ResponseEntity<Void> marcarLido(@PathVariable Long id) {
        // Leitura individual de aviso é registrada client-side; endpoint mantido por contrato
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    @Operation(summary = "Remove aviso")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        avisoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private Long extrairUsuarioId(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        String token  = header != null && header.startsWith("Bearer ") ? header.substring(7) : "";
        return jwtService.extractUserId(token);
    }
}
