package br.edu.waldorf.modules.lgpd.api;

import br.edu.waldorf.modules.lgpd.domain.model.ConsentimentoLgpd;
import br.edu.waldorf.modules.lgpd.domain.model.SolicitacaoTitular;
import br.edu.waldorf.modules.lgpd.domain.service.LgpdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Map;

/**
 * Controller REST para LGPD
 * Base path: /api/v1/lgpd
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/lgpd")
@RequiredArgsConstructor
@Tag(name = "LGPD", description = "Consentimentos e solicitacoes de titulares de dados")
public class LgpdController {

    private final LgpdService lgpdService;

    // ---- Consentimentos ----

    @GetMapping("/consents/{pessoaId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Listar consentimentos ativos de uma pessoa")
    public ResponseEntity<List<ConsentimentoLgpd>> consentimentosAtivos(@PathVariable Long pessoaId) {
        return ResponseEntity.ok(lgpdService.listarConsentimentosAtivos(pessoaId));
    }

    @PostMapping("/consents")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Registrar consentimento LGPD")
    public ResponseEntity<ConsentimentoLgpd> registrarConsentimento(
            @Valid @RequestBody ConsentimentoLgpd consentimento,
            HttpServletRequest request,
            UriComponentsBuilder ucb
    ) {
        String ip = request.getRemoteAddr();
        String versao = request.getHeader("X-Terms-Version");
        ConsentimentoLgpd salvo = lgpdService.registrarConsentimento(consentimento, ip, versao, null);
        var uri = ucb.path("/api/v1/lgpd/consents/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(salvo);
    }

    @PatchMapping("/consents/{id}/revoke")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Revogar consentimento")
    public ResponseEntity<ConsentimentoLgpd> revogar(@PathVariable Long id) {
        return ResponseEntity.ok(lgpdService.revogarConsentimento(id));
    }

    // ---- Solicitacoes de Titulares ----

    @GetMapping("/requests")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Listar solicitacoes em aberto")
    public ResponseEntity<List<SolicitacaoTitular>> emAberto() {
        return ResponseEntity.ok(lgpdService.listarEmAberto());
    }

    @GetMapping("/requests/{pessoaId}/history")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Historico de solicitacoes de uma pessoa")
    public ResponseEntity<Page<SolicitacaoTitular>> historico(
            @PathVariable Long pessoaId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(lgpdService.listarPorPessoa(pessoaId, pageable));
    }

    @PostMapping("/requests")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Abrir nova solicitacao LGPD (prazo: 15 dias)")
    public ResponseEntity<SolicitacaoTitular> abrir(
            @Valid @RequestBody SolicitacaoTitular solicitacao,
            UriComponentsBuilder ucb
    ) {
        SolicitacaoTitular salva = lgpdService.abrirSolicitacao(solicitacao);
        var uri = ucb.path("/api/v1/lgpd/requests/{id}").buildAndExpand(salva.getId()).toUri();
        return ResponseEntity.created(uri).body(salva);
    }

    @PatchMapping("/requests/{id}/advance")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Avancar status da solicitacao (ABERTA->EM_ANALISE->EM_ATENDIMENTO)")
    public ResponseEntity<SolicitacaoTitular> avancar(@PathVariable Long id) {
        return ResponseEntity.ok(lgpdService.avancarStatus(id));
    }

    @PatchMapping("/requests/{id}/conclude")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Concluir solicitacao com resposta")
    public ResponseEntity<SolicitacaoTitular> concluir(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(lgpdService.concluir(id, body.get("resposta"), null));
    }

    @PatchMapping("/requests/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Rejeitar solicitacao com justificativa")
    public ResponseEntity<SolicitacaoTitular> rejeitar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(lgpdService.rejeitar(id, body.get("justificativa"), null));
    }
}
