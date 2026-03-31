package br.edu.waldorf.api.controller;

import br.edu.waldorf.application.dto.epoca.EpocaRequestDTO;
import br.edu.waldorf.application.dto.epoca.EpocaResponseDTO;
import br.edu.waldorf.application.service.EpocaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/epocas")
@RequiredArgsConstructor
@Tag(name = "Épocas Pedagógicas", description = "Épocas de ensino Waldorf por turma")
public class EpocaController {

    private final EpocaService epocaService;

    @GetMapping
    @Operation(summary = "Listar épocas, filtradas por turma e status")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','PROFESSOR')")
    public ResponseEntity<List<EpocaResponseDTO>> listar(
            @RequestParam(required = false) Long turmaId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(epocaService.listar(turmaId, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar época por ID")
    public ResponseEntity<EpocaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(epocaService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar época pedagógica")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','PROFESSOR')")
    public ResponseEntity<EpocaResponseDTO> criar(@Valid @RequestBody EpocaRequestDTO dto) {
        return ResponseEntity.ok(epocaService.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar época")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','PROFESSOR')")
    public ResponseEntity<EpocaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EpocaRequestDTO dto) {
        return ResponseEntity.ok(epocaService.atualizar(id, dto));
    }

    @PostMapping("/{id}/encerrar")
    @Operation(summary = "Encerrar época")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<EpocaResponseDTO> encerrar(@PathVariable Long id) {
        return ResponseEntity.ok(epocaService.encerrar(id));
    }
}
