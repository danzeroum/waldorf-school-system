package br.edu.waldorf.api.controller;

import br.edu.waldorf.application.dto.aluno.AlunoRequestDTO;
import br.edu.waldorf.application.dto.aluno.AlunoResponseDTO;
import br.edu.waldorf.application.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alunos")
@RequiredArgsConstructor
@Tag(name = "Alunos", description = "Gestão de alunos")
public class AlunoController {

    private final AlunoService alunoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','PROFESSOR')")
    @Operation(summary = "Listar alunos com filtros e paginação")
    public ResponseEntity<Page<AlunoResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Long turmaId,
            @RequestParam(required = false) Boolean ativo,
            Pageable pageable) {
        return ResponseEntity.ok(alunoService.listar(nome, turmaId, ativo, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','PROFESSOR','PAIS')")
    @Operation(summary = "Buscar aluno por ID")
    public ResponseEntity<AlunoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(alunoService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Cadastrar aluno")
    public ResponseEntity<AlunoResponseDTO> criar(@Valid @RequestBody AlunoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alunoService.criar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Atualizar aluno")
    public ResponseEntity<AlunoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AlunoRequestDTO dto) {
        return ResponseEntity.ok(alunoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Inativar aluno")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        alunoService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
