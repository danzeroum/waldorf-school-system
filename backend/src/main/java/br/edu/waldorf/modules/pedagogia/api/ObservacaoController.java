package br.edu.waldorf.modules.pedagogia.api;

import br.edu.waldorf.modules.pedagogia.domain.model.ObservacaoDesenvolvimento;
import br.edu.waldorf.modules.pedagogia.domain.service.ObservacaoDesenvolvimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller REST para Observações de Desenvolvimento
 * Base path: /api/v1/observations
 */
@RestController
@RequestMapping("/api/v1/observations")
@RequiredArgsConstructor
@Tag(name = "Observações", description = "Observações de desenvolvimento Waldorf")
public class ObservacaoController {

    private final ObservacaoDesenvolvimentoService service;

    @GetMapping("/student/{alunoId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','DIRECAO')")
    @Operation(summary = "Listar observações de um aluno com filtros")
    public ResponseEntity<Page<ObservacaoDesenvolvimento>> listar(
            @PathVariable Long alunoId,
            @RequestParam(required = false) ObservacaoDesenvolvimento.AspectoDensenvolvimento aspecto,
            @RequestParam(required = false) Long professorId,
            @PageableDefault(size = 20, sort = "dataObservacao", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.listarComFiltros(alunoId, aspecto, professorId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','DIRECAO')")
    @Operation(summary = "Buscar observação por ID")
    public ResponseEntity<ObservacaoDesenvolvimento> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/teacher/{professorId}/recent")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','DIRECAO')")
    @Operation(summary = "Listar observações recentes do professor (padrão: 30 dias)")
    public ResponseEntity<List<ObservacaoDesenvolvimento>> recentes(
            @PathVariable Long professorId,
            @RequestParam(defaultValue = "30") int dias
    ) {
        return ResponseEntity.ok(service.listarRecentesProfessor(professorId, dias));
    }

    @GetMapping("/class/{turmaId}/period")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','DIRECAO')")
    @Operation(summary = "Listar observações de uma turma por período")
    public ResponseEntity<List<ObservacaoDesenvolvimento>> porPeriodo(
            @PathVariable Long turmaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
    ) {
        return ResponseEntity.ok(service.listarPorTurmaEPeriodo(turmaId, inicio, fim));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @Operation(summary = "Registrar observação de desenvolvimento")
    public ResponseEntity<ObservacaoDesenvolvimento> registrar(
            @Valid @RequestBody ObservacaoDesenvolvimento obs,
            UriComponentsBuilder ucb
    ) {
        ObservacaoDesenvolvimento salva = service.registrar(obs);
        var uri = ucb.path("/api/v1/observations/{id}").buildAndExpand(salva.getId()).toUri();
        return ResponseEntity.created(uri).body(salva);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @Operation(summary = "Atualizar observação")
    public ResponseEntity<ObservacaoDesenvolvimento> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ObservacaoDesenvolvimento dados
    ) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir observação")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
