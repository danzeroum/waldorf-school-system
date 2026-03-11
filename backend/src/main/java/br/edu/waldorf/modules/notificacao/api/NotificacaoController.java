package br.edu.waldorf.modules.notificacao.api;

import br.edu.waldorf.modules.notificacao.domain.model.LogEnvioNotificacao;
import br.edu.waldorf.modules.notificacao.domain.model.PreferenciaNotificacao;
import br.edu.waldorf.modules.notificacao.domain.repository.PreferenciaNotificacaoRepository;
import br.edu.waldorf.modules.notificacao.domain.service.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para Notificacoes
 * Base path: /api/v1/notifications
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificacoes", description = "Central de notificacoes e preferencias")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;
    private final PreferenciaNotificacaoRepository preferenciaRepository;

    @GetMapping("/user/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN') or #usuarioId == authentication.principal.id")
    @Operation(summary = "Listar notificacoes do usuario")
    public ResponseEntity<Page<LogEnvioNotificacao>> listar(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(notificacaoService.listarPorUsuario(usuarioId, pageable));
    }

    @GetMapping("/user/{usuarioId}/unread-count")
    @PreAuthorize("hasAnyRole('ADMIN') or #usuarioId == authentication.principal.id")
    @Operation(summary = "Contar notificacoes nao lidas")
    public ResponseEntity<Map<String, Long>> contarNaoLidas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(Map.of("unread", notificacaoService.countNaoLidas(usuarioId)));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Marcar notificacao como lida")
    public ResponseEntity<Void> marcarLido(@PathVariable Long id) {
        notificacaoService.marcarComoLido(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/preferences/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN') or #usuarioId == authentication.principal.id")
    @Operation(summary = "Listar preferencias de notificacao do usuario")
    public ResponseEntity<List<PreferenciaNotificacao>> preferencias(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(preferenciaRepository.findByUsuarioIdAndAtivoTrue(usuarioId));
    }

    @PutMapping("/preferences")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Salvar preferencia de notificacao")
    public ResponseEntity<PreferenciaNotificacao> salvarPreferencia(
            @RequestBody PreferenciaNotificacao preferencia
    ) {
        // Upsert: verifica se ja existe para usuario+categoria
        var existente = preferenciaRepository.findByUsuarioIdAndCategoria(
                preferencia.getUsuario().getId(), preferencia.getCategoria()
        );
        if (existente.isPresent()) {
            PreferenciaNotificacao p = existente.get();
            p.setCanalEmail(preferencia.getCanalEmail());
            p.setCanalPush(preferencia.getCanalPush());
            p.setCanalSms(preferencia.getCanalSms());
            p.setAgregacao(preferencia.getAgregacao());
            p.setHorarioResumo(preferencia.getHorarioResumo());
            p.setSilencioInicio(preferencia.getSilencioInicio());
            p.setSilencioFim(preferencia.getSilencioFim());
            p.setAtivo(preferencia.getAtivo());
            return ResponseEntity.ok(preferenciaRepository.save(p));
        }
        return ResponseEntity.ok(preferenciaRepository.save(preferencia));
    }
}
