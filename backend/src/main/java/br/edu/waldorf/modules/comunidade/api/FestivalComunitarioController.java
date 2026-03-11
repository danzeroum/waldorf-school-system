package br.edu.waldorf.modules.comunidade.api;

import br.edu.waldorf.modules.comunidade.domain.model.FestivalComunitario;
import br.edu.waldorf.modules.comunidade.domain.model.InscricaoEvento;
import br.edu.waldorf.modules.comunidade.domain.repository.InscricaoEventoRepository;
import br.edu.waldorf.modules.comunidade.domain.service.FestivalComunitarioService;
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
 * Controller REST para Festivais Comunitarios
 * Base path: /api/v1/festivals
 */
@RestController
@RequestMapping("/api/v1/festivals")
@RequiredArgsConstructor
@Tag(name = "Festivais Comunitários", description = "Eventos sazonais Waldorf")
public class FestivalComunitarioController {

    private final FestivalComunitarioService service;
    private final InscricaoEventoRepository inscricaoRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar proximos festivais")
    public ResponseEntity<List<FestivalComunitario>> listar() {
        return ResponseEntity.ok(service.listarProximos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar festival por ID")
    public ResponseEntity<FestivalComunitario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/{id}/registrations")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Listar inscricoes do festival")
    public ResponseEntity<List<InscricaoEvento>> inscricoes(@PathVariable Long id) {
        return ResponseEntity.ok(
                inscricaoRepository.findByTipoEventoAndEventoId(InscricaoEvento.TipoEvento.FESTIVAL, id)
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Criar festival comunitario")
    public ResponseEntity<FestivalComunitario> criar(
            @Valid @RequestBody FestivalComunitario festival,
            UriComponentsBuilder ucb
    ) {
        FestivalComunitario salvo = service.criar(festival);
        var uri = ucb.path("/api/v1/festivals/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(salvo);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Confirmar festival")
    public ResponseEntity<Void> confirmar(@PathVariable Long id) {
        service.confirmar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/conclude")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Concluir festival")
    public ResponseEntity<Void> concluir(@PathVariable Long id) {
        service.concluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Cancelar festival")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/register")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Inscrever-se em um festival")
    public ResponseEntity<InscricaoEvento> inscrever(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        Long pessoaId    = Long.valueOf(body.get("pessoaId").toString());
        int numPessoas   = Integer.parseInt(body.getOrDefault("numeroPessoas", 1).toString());
        int criancas     = Integer.parseInt(body.getOrDefault("criancasIncluidas", 0).toString());
        InscricaoEvento inscricao = service.inscrever(id, pessoaId, numPessoas, criancas);
        return ResponseEntity.ok(inscricao);
    }
}
