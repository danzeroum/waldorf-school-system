package com.waldorf.presentation.controller;

import com.waldorf.application.dto.aluno.AlunoRequestDTO;
import com.waldorf.application.dto.aluno.AlunoResponseDTO;
import com.waldorf.application.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/alunos")
@RequiredArgsConstructor
@Tag(name = "Alunos", description = "Gestão de alunos matriculados")
public class AlunoController {

    private final AlunoService alunoService;

    @GetMapping
    @Operation(summary = "Listar alunos com paginação e filtros")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','PROFESSOR')")
    public ResponseEntity<Page<AlunoResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Long turmaId,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(alunoService.listar(nome, turmaId, ativo, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar aluno por ID")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','PROFESSOR','PAIS')")
    public ResponseEntity<AlunoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alunoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo aluno")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<AlunoResponseDTO> criar(@Valid @RequestBody AlunoRequestDTO dto) {
        AlunoResponseDTO criado = alunoService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados do aluno")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<AlunoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AlunoRequestDTO dto) {
        return ResponseEntity.ok(alunoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar aluno (soft delete)")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        alunoService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/responsaveis")
    @Operation(summary = "Listar responsáveis do aluno")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','PROFESSOR')")
    public ResponseEntity<?> listarResponsaveis(@PathVariable Long id) {
        return ResponseEntity.ok(alunoService.listarResponsaveis(id));
    }

    @PostMapping("/{id}/responsaveis/{responsavelId}")
    @Operation(summary = "Vincular responsável ao aluno")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<Void> vincularResponsavel(
            @PathVariable Long id,
            @PathVariable Long responsavelId,
            @RequestParam String parentesco) {
        alunoService.vincularResponsavel(id, responsavelId, parentesco);
        return ResponseEntity.noContent().build();
    }
}
