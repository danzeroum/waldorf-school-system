package br.edu.waldorf.modules.pedagogia.api;

import br.edu.waldorf.modules.pedagogia.domain.model.RelatorioNarrativo;
import br.edu.waldorf.modules.pedagogia.domain.service.RelatorioNarrativoService;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Controller REST para Relatórios Narrativos Waldorf
 * Base path: /api/v1/narrative-reports
 */
@RestController
@RequestMapping("/api/v1/narrative-reports")
@RequiredArgsConstructor
@Tag(name = "Relatórios Narrativos", description = "Relatórios narrativos Waldorf")
public class RelatorioNarrativoController {

    private final RelatorioNarrativoService service;

    @GetMapping("/student/{alunoId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','DIRECAO','SECRETARIA')")
    @Operation(summary = "Histórico de relatórios de um aluno")
    public ResponseEntity<List<RelatorioNarrativo>> listarPorAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(service.listarPorAluno(alunoId));
    }

    @GetMapping("/class/{turmaId}/cycle/{ciclo}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','DIRECAO')")
    @Operation(summary = "Listar relatórios de uma turma por ciclo")
    public ResponseEntity<List<RelatorioNarrativo>> listarPorTurmaECiclo(
            @PathVariable Long turmaId,
            @PathVariable String ciclo
    ) {
        return ResponseEntity.ok(service.listarPorTurmaECiclo(turmaId, ciclo));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO','SECRETARIA')")
    @Operation(summary = "Listar relatórios por status")
    public ResponseEntity<Page<RelatorioNarrativo>> listarPorStatus(
            @RequestParam(required = false) RelatorioNarrativo.StatusRelatorio status,
            @PageableDefault(size = 25) Pageable pageable
    ) {
        return ResponseEntity.ok(service.listarPorStatus(status, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','DIRECAO','SECRETARIA')")
    @Operation(summary = "Buscar relatório por ID")
    public ResponseEntity<RelatorioNarrativo> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @Operation(summary = "Criar relatório narrativo")
    public ResponseEntity<RelatorioNarrativo> criar(
            @Valid @RequestBody RelatorioNarrativo relatorio,
            UriComponentsBuilder ucb
    ) {
        RelatorioNarrativo salvo = service.criar(relatorio);
        var uri = ucb.path("/api/v1/narrative-reports/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(salvo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @Operation(summary = "Atualizar relatório narrativo")
    public ResponseEntity<RelatorioNarrativo> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RelatorioNarrativo dados
    ) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @PatchMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @Operation(summary = "Enviar relatório para revisão")
    public ResponseEntity<Void> enviarRevisao(@PathVariable Long id) {
        service.enviarParaRevisao(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Aprovar relatório")
    public ResponseEntity<Void> aprovar(@PathVariable Long id) {
        service.aprovar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deliver")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Marcar relatório como entregue aos pais")
    public ResponseEntity<Void> entregar(@PathVariable Long id) {
        service.entregar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirm-reading")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','RESPONSAVEL')")
    @Operation(summary = "Confirmar leitura do relatório pelo responsável")
    public ResponseEntity<Void> confirmarLeitura(@PathVariable Long id) {
        service.confirmarLeitura(id);
        return ResponseEntity.noContent().build();
    }
}
