package com.waldorf.presentation.controller;

import com.waldorf.application.dto.DashboardSecretariaDTO;
import com.waldorf.domain.enums.StatusConsentimento;
import com.waldorf.infrastructure.repository.AlunoRepository;
import com.waldorf.infrastructure.repository.ConsentimentoLgpdRepository;
import com.waldorf.infrastructure.repository.ContratoRepository;
import com.waldorf.infrastructure.repository.TurmaRepository;
import com.waldorf.infrastructure.security.JwtService;
import com.waldorf.infrastructure.repository.UsuarioRepository;
import com.waldorf.infrastructure.repository.NotificacaoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Métricas agregadas para o dashboard")
public class AnalyticsController {

    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final ContratoRepository contratoRepository;
    private final ConsentimentoLgpdRepository consentimentoRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/dashboard/secretaria")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Retorna métricas do dashboard para a secretaria")
    public DashboardSecretariaDTO getDashboardSecretaria(HttpServletRequest req) {
        Long usuarioId = extrairUsuarioId(req);
        int anoAtual = LocalDate.now().getYear();
        int diaAtual = LocalDate.now().getDayOfMonth();

        long turmasAtivas = turmaRepository.findAll().stream()
            .filter(t -> t.getAnoLetivo() == anoAtual)
            .count();

        long mensalidadesAtrasadas = contratoRepository.findAll().stream()
            .filter(c -> c.getSituacao() != null
                && c.getSituacao().name().equals("ATIVO")
                && c.getDiaVencimento() > 0
                && c.getDiaVencimento() < diaAtual)
            .count();

        long contratosPendentes = contratoRepository.findAll().stream()
            .filter(c -> c.getSituacao() != null
                && c.getSituacao().name().equals("ATIVO"))
            .count();

        return DashboardSecretariaDTO.builder()
            .totalAlunosAtivos(alunoRepository.count())
            .matriculasAtivas(turmasAtivas)
            .contratosPendentes(contratosPendentes)
            .mensalidadesAtrasadas(mensalidadesAtrasadas)
            .lgpdPendentes(consentimentoRepository.countByStatus(StatusConsentimento.PENDENTE))
            .notificacoesNaoLidas(notificacaoRepository.countByUsuarioIdAndLidaFalse(usuarioId))
            .turmasAtivas(turmasAtivas)
            .build();
    }

    private Long extrairUsuarioId(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        String token = header != null && header.startsWith("Bearer ")
            ? header.substring(7) : "";
        String email = jwtService.extractUsername(token);
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + email))
            .getId();
    }
}
