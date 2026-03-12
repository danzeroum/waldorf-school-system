package com.waldorf.presentation.controller;

import com.waldorf.application.dto.observacao.ObservacaoRequestDTO;
import com.waldorf.application.dto.observacao.ObservacaoResponseDTO;
import com.waldorf.application.service.ObservacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/observacoes")
@RequiredArgsConstructor
@Tag(name = "Observações", description = "Observações pedagógicas por aluno")
public class ObservacaoController {

    private final ObservacaoService observacaoService;

    @GetMapping
    @Operation(summary = "Listar todas as observações ou filtrar por aluno")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','PROFESSOR')")
    public ResponseEntity<List<ObservacaoResponseDTO>> listar(
            @RequestParam(required = false) Long alunoId,
            @RequestParam(required = false) String aspecto) {
        if (alunoId != null) {
            return ResponseEntity.ok(observacaoService.listarPorAluno(alunoId, aspecto));
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/aluno/{alunoId}")
    @Operation(summary = "Listar observações de um aluno")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','PROFESSOR')")
    public ResponseEntity<List<ObservacaoResponseDTO>> listarPorAluno(
            @PathVariable Long alunoId,
            @RequestParam(required = false) String aspecto) {
        return ResponseEntity.ok(observacaoService.listarPorAluno(alunoId, aspecto));
    }

    @PostMapping
    @Operation(summary = "Registrar observação")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<ObservacaoResponseDTO> criar(@Valid @RequestBody ObservacaoRequestDTO dto) {
        return ResponseEntity.ok(observacaoService.criar(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar observação por ID")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','PROFESSOR')")
    public ResponseEntity<ObservacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(observacaoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<ObservacaoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ObservacaoRequestDTO dto) {
        return ResponseEntity.ok(observacaoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        observacaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
