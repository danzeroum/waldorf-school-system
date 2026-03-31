package br.edu.waldorf.api.controller;

import br.edu.waldorf.application.dto.turma.TurmaRequestDTO;
import br.edu.waldorf.application.dto.turma.TurmaResponseDTO;
import br.edu.waldorf.application.service.TurmaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/turmas")
@RequiredArgsConstructor
@Tag(name = "Turmas", description = "Gestão de turmas e classes Waldorf")
public class TurmaController {

    private final TurmaService turmaService;

    @GetMapping
    @Operation(summary = "Listar turmas, filtradas opcionalmente por ano letivo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TurmaResponseDTO>> listar(
            @RequestParam(required = false) Integer anoLetivo) {
        return ResponseEntity.ok(turmaService.listar(anoLetivo));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar turma por ID")
    public ResponseEntity<TurmaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(turmaService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar turma")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR')")
    public ResponseEntity<TurmaResponseDTO> criar(@Valid @RequestBody TurmaRequestDTO dto) {
        return ResponseEntity.ok(turmaService.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar turma")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR')")
    public ResponseEntity<TurmaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody TurmaRequestDTO dto) {
        return ResponseEntity.ok(turmaService.atualizar(id, dto));
    }

    @GetMapping("/{id}/alunos")
    @Operation(summary = "Listar alunos da turma")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','PROFESSOR')")
    public ResponseEntity<?> listarAlunos(@PathVariable Long id) {
        return ResponseEntity.ok(turmaService.listarAlunos(id));
    }
}
