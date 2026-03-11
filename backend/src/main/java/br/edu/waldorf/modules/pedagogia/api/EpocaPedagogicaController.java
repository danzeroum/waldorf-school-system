package br.edu.waldorf.modules.pedagogia.api;

import br.edu.waldorf.modules.pedagogia.domain.model.EpocaPedagogica;
import br.edu.waldorf.modules.pedagogia.domain.service.EpocaPedagogicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para Épocas Pedagógicas
 * Base path: /api/v1/pedagogical-epochs
 */
@RestController
@RequestMapping("/api/v1/pedagogical-epochs")
@RequiredArgsConstructor
@Tag(name = "Épocas Pedagógicas", description = "Gestão das épocas Waldorf")
public class EpocaPedagogicaController {

    private final EpocaPedagogicaService service;

    @GetMapping("/class/{turmaId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Listar épocas de uma turma")
    public ResponseEntity<List<EpocaPedagogica>> listarPorTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(service.listarPorTurma(turmaId));
    }

    @GetMapping("/class/{turmaId}/current")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Buscar época em andamento de uma turma")
    public ResponseEntity<EpocaPedagogica> buscarEmAndamento(@PathVariable Long turmaId) {
        return service.buscarEmAndamento(turmaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Buscar época por ID")
    public ResponseEntity<EpocaPedagogica> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','DIRECAO')")
    @Operation(summary = "Criar nova época pedagógica")
    public ResponseEntity<EpocaPedagogica> criar(
            @Valid @RequestBody EpocaPedagogica epoca,
            UriComponentsBuilder ucb
    ) {
        EpocaPedagogica salva = service.criar(epoca);
        var uri = ucb.path("/api/v1/pedagogical-epochs/{id}").buildAndExpand(salva.getId()).toUri();
        return ResponseEntity.created(uri).body(salva);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','DIRECAO')")
    @Operation(summary = "Atualizar época pedagógica")
    public ResponseEntity<EpocaPedagogica> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EpocaPedagogica dados
    ) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @PatchMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','DIRECAO')")
    @Operation(summary = "Iniciar época (PLANEJADA -> EM_ANDAMENTO)")
    public ResponseEntity<Void> iniciar(@PathVariable Long id) {
        service.iniciar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/conclude")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','DIRECAO')")
    @Operation(summary = "Concluir época")
    public ResponseEntity<Void> concluir(@PathVariable Long id) {
        service.concluir(id);
        return ResponseEntity.noContent().build();
    }
}
