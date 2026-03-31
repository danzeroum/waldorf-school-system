package com.waldorf.presentation.controller;

import com.waldorf.application.dto.NotificacaoDTO;
import com.waldorf.application.dto.PreferenciaNotificacaoDTO;
import com.waldorf.application.service.NotificacaoService;
import com.waldorf.domain.entity.Usuario;
import com.waldorf.infrastructure.repository.UsuarioRepository;
import com.waldorf.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notificações do usuário")
public class NotificationController {

    private final NotificacaoService notificacaoService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    @Operation(summary = "Lista notificações do usuário autenticado")
    public ResponseEntity<List<NotificacaoDTO>> listar(HttpServletRequest req) {
        return ResponseEntity.ok(notificacaoService.listar(extrairUsuarioId(req)));
    }

    @GetMapping("/count")
    @Operation(summary = "Conta notificações não lidas")
    public ResponseEntity<Map<String, Long>> contarNaoLidas(HttpServletRequest req) {
        return ResponseEntity.ok(Map.of("total", notificacaoService.contarNaoLidas(extrairUsuarioId(req))));
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "Marca uma notificação como lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id, HttpServletRequest req) {
        notificacaoService.marcarComoLida(id, extrairUsuarioId(req));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/read-all")
    @Operation(summary = "Marca todas as notificações como lidas")
    public ResponseEntity<Void> marcarTodasComoLidas(HttpServletRequest req) {
        notificacaoService.marcarTodasComoLidas(extrairUsuarioId(req));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/preferences")
    @Operation(summary = "Busca preferências de notificação")
    public ResponseEntity<PreferenciaNotificacaoDTO> buscarPreferencias(HttpServletRequest req) {
        return ResponseEntity.ok(notificacaoService.buscarPreferencias(extrairUsuarioId(req)));
    }

    @PutMapping("/preferences")
    @Operation(summary = "Salva preferências de notificação")
    public ResponseEntity<PreferenciaNotificacaoDTO> salvarPreferencias(
            @RequestBody PreferenciaNotificacaoDTO dto,
            HttpServletRequest req) {
        return ResponseEntity.ok(notificacaoService.salvarPreferencias(extrairUsuarioId(req), dto));
    }

    // FIX: JwtService não tem extractUserId() → usa extractUsername() (email) + busca por email
    private Long extrairUsuarioId(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        String token  = header != null && header.startsWith("Bearer ") ? header.substring(7) : "";
        String email  = jwtService.extractUsername(token);
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + email));
        return u.getId();
    }
}
