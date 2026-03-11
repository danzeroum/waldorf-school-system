package br.edu.waldorf.modules.comunidade.api;

import br.edu.waldorf.modules.comunidade.domain.model.InscricaoEvento;
import br.edu.waldorf.modules.comunidade.domain.model.Mutirao;
import br.edu.waldorf.modules.comunidade.domain.repository.InscricaoEventoRepository;
import br.edu.waldorf.modules.comunidade.domain.service.MutiraoService;
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
 * Controller REST para Mutiroes Comunitarios
 * Base path: /api/v1/work-parties
 */
@RestController
@RequestMapping("/api/v1/work-parties")
@RequiredArgsConstructor
@Tag(name = "Mutirões", description = "Mutiroes comunitarios Waldorf")
public class MutiraoController {

    private final MutiraoService service;
    private final InscricaoEventoRepository inscricaoRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar proximos mutiroes")
    public ResponseEntity<List<Mutirao>> listar() {
        return ResponseEntity.ok(service.listarProximos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar mutirao por ID")
    public ResponseEntity<Mutirao> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/{id}/registrations")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Listar inscricoes do mutirao")
    public ResponseEntity<List<InscricaoEvento>> inscricoes(@PathVariable Long id) {
        return ResponseEntity.ok(
                inscricaoRepository.findByTipoEventoAndEventoId(InscricaoEvento.TipoEvento.MUTIRAO, id)
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Criar mutirao")
    public ResponseEntity<Mutirao> criar(
            @Valid @RequestBody Mutirao mutirao,
            UriComponentsBuilder ucb
    ) {
        Mutirao salvo = service.criar(mutirao);
        var uri = ucb.path("/api/v1/work-parties/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(salvo);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Confirmar mutirao")
    public ResponseEntity<Void> confirmar(@PathVariable Long id) {
        service.confirmar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/conclude")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Concluir mutirao")
    public ResponseEntity<Void> concluir(@PathVariable Long id) {
        service.concluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Cancelar mutirao")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/register")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Inscrever-se em um mutirao")
    public ResponseEntity<InscricaoEvento> inscrever(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        Long pessoaId    = Long.valueOf(body.get("pessoaId").toString());
        int numPessoas   = Integer.parseInt(body.getOrDefault("numeroPessoas", 1).toString());
        int criancas     = Integer.parseInt(body.getOrDefault("criancasIncluidas", 0).toString());
        String materiais = body.getOrDefault("materiaisTrazidos", "").toString();
        InscricaoEvento inscricao = service.inscrever(id, pessoaId, numPessoas, criancas, materiais);
        return ResponseEntity.ok(inscricao);
    }
}
