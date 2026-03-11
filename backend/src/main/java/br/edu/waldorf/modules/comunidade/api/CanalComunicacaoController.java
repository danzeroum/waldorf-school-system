package br.edu.waldorf.modules.comunidade.api;

import br.edu.waldorf.modules.comunidade.domain.model.CanalComunicacao;
import br.edu.waldorf.modules.comunidade.domain.model.MensagemCanal;
import br.edu.waldorf.modules.comunidade.domain.service.CanalComunicacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para Canais de Comunicacao
 * Base path: /api/v1/channels
 */
@RestController
@RequestMapping("/api/v1/channels")
@RequiredArgsConstructor
@Tag(name = "Canais de Comunicacao", description = "Mensagens por canal Waldorf")
public class CanalComunicacaoController {

    private final CanalComunicacaoService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar canais ativos")
    public ResponseEntity<List<CanalComunicacao>> listar() {
        return ResponseEntity.ok(service.listarAtivos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar canal por ID")
    public ResponseEntity<CanalComunicacao> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/{id}/messages")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar mensagens do canal")
    public ResponseEntity<Page<MensagemCanal>> mensagens(
            @PathVariable Long id,
            @PageableDefault(size = 30, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.listarMensagens(id, pageable));
    }

    @GetMapping("/{id}/pinned")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar mensagens fixadas do canal")
    public ResponseEntity<List<MensagemCanal>> fixadas(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarFixadas(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO','SECRETARIA')")
    @Operation(summary = "Criar novo canal")
    public ResponseEntity<CanalComunicacao> criar(
            @Valid @RequestBody CanalComunicacao canal,
            UriComponentsBuilder ucb
    ) {
        CanalComunicacao salvo = service.criar(canal);
        var uri = ucb.path("/api/v1/channels/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(salvo);
    }

    @PostMapping("/{id}/messages")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Enviar mensagem ao canal")
    public ResponseEntity<MensagemCanal> enviarMensagem(
            @PathVariable Long id,
            @RequestBody MensagemCanal mensagem,
            UriComponentsBuilder ucb
    ) {
        mensagem.setCanal(service.buscarPorId(id));
        MensagemCanal salva = service.enviarMensagem(mensagem);
        var uri = ucb.path("/api/v1/channels/{id}/messages").buildAndExpand(id).toUri();
        return ResponseEntity.created(uri).body(salva);
    }

    @PutMapping("/messages/{msgId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Editar mensagem")
    public ResponseEntity<MensagemCanal> editarMensagem(
            @PathVariable Long msgId,
            @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(service.editarMensagem(msgId, body.get("conteudo")));
    }

    @PatchMapping("/messages/{msgId}/pin")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO','PROFESSOR')")
    @Operation(summary = "Fixar/desafixar mensagem")
    public ResponseEntity<Void> fixar(
            @PathVariable Long msgId,
            @RequestParam(defaultValue = "true") boolean fixar
    ) {
        service.fixarMensagem(msgId, fixar);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/messages/{msgId}")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Excluir mensagem")
    public ResponseEntity<Void> excluir(@PathVariable Long msgId) {
        service.excluirMensagem(msgId);
        return ResponseEntity.noContent().build();
    }
}
