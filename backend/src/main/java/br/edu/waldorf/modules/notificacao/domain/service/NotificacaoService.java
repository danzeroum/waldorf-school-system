package br.edu.waldorf.modules.notificacao.domain.service;

import br.edu.waldorf.modules.notificacao.domain.model.LogEnvioNotificacao;
import br.edu.waldorf.modules.notificacao.domain.model.PreferenciaNotificacao;
import br.edu.waldorf.modules.notificacao.domain.repository.LogEnvioNotificacaoRepository;
import br.edu.waldorf.modules.notificacao.domain.repository.PreferenciaNotificacaoRepository;
import br.edu.waldorf.modules.security.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Service de dominio para Notificacoes
 * Gerencia criacao, supressao por preferencia e processamento agendado
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final LogEnvioNotificacaoRepository logRepository;
    private final PreferenciaNotificacaoRepository preferenciaRepository;

    // -------------------------------------------------------------------------
    // Criacao de notificacao
    // -------------------------------------------------------------------------

    @Transactional
    public LogEnvioNotificacao criar(
            Usuario usuario,
            LogEnvioNotificacao.TipoConteudo tipo,
            PreferenciaNotificacao.CanalEnvio canal,
            PreferenciaNotificacao.CategoriaNotificacao categoria,
            String titulo,
            String conteudo,
            String referenciaTipo,
            Long referenciaId
    ) {
        LocalTime agora = LocalTime.now();
        LocalDateTime planejado = LocalDateTime.now();

        // Verifica preferencia do usuario
        var prefOpt = preferenciaRepository.findByUsuarioIdAndCategoria(usuario.getId(), categoria);

        if (prefOpt.isPresent()) {
            PreferenciaNotificacao pref = prefOpt.get();

            if (!Boolean.TRUE.equals(pref.getAtivo())) {
                return criarSuprimido(usuario, tipo, canal, titulo, conteudo,
                        referenciaTipo, referenciaId, "Notificacoes desativadas para categoria " + categoria);
            }

            if (!pref.aceitaCanal(canal)) {
                return criarSuprimido(usuario, tipo, canal, titulo, conteudo,
                        referenciaTipo, referenciaId, "Canal " + canal + " desabilitado pelo usuario");
            }

            if (pref.estaEmSilencio(agora)) {
                // Agenda para apos o fim do silencio
                planejado = LocalDateTime.now().toLocalDate().atTime(pref.getSilencioFim()).plusMinutes(1);
                if (planejado.isBefore(LocalDateTime.now())) {
                    planejado = planejado.plusDays(1);
                }
            }

            if (pref.getAgregacao() == PreferenciaNotificacao.Agregacao.RESUMO_DIARIO) {
                planejado = LocalDateTime.now().toLocalDate().atTime(pref.getHorarioResumo());
                if (planejado.isBefore(LocalDateTime.now())) planejado = planejado.plusDays(1);
            }
        }

        LogEnvioNotificacao log = LogEnvioNotificacao.builder()
                .usuario(usuario)
                .tipoConteudo(tipo)
                .canal(canal)
                .titulo(titulo)
                .conteudo(conteudo)
                .statusEnvio(LogEnvioNotificacao.StatusEnvio.PENDENTE)
                .dataHoraEnvioPlanejado(planejado)
                .referenciaTipo(referenciaTipo)
                .referenciaId(referenciaId)
                .build();

        return logRepository.save(log);
    }

    // -------------------------------------------------------------------------
    // Consultas
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public Page<LogEnvioNotificacao> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return logRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId, pageable);
    }

    @Transactional(readOnly = true)
    public long countNaoLidas(Long usuarioId) {
        return logRepository.countByUsuarioIdAndStatusEnvio(
                usuarioId, LogEnvioNotificacao.StatusEnvio.ENTREGUE
        );
    }

    @Transactional
    public void marcarComoLido(Long logId) {
        logRepository.findById(logId).ifPresent(l -> {
            l.marcarLido();
            logRepository.save(l);
        });
    }

    // -------------------------------------------------------------------------
    // Job agendado: processa pendentes a cada 2 minutos
    // -------------------------------------------------------------------------

    @Scheduled(fixedDelay = 120_000)
    @Transactional
    public void processarPendentes() {
        List<LogEnvioNotificacao> pendentes = logRepository.findPendentesParaEnvio();
        for (LogEnvioNotificacao notif : pendentes) {
            try {
                enviarPorCanal(notif);
                notif.marcarEnviado();
            } catch (Exception e) {
                notif.marcarFalha(e.getMessage());
                log.warn("Falha ao enviar notificacao id={}: {}", notif.getId(), e.getMessage());
            }
            logRepository.save(notif);
        }
        if (!pendentes.isEmpty()) {
            log.info("Notificacoes processadas: {}", pendentes.size());
        }
    }

    // -------------------------------------------------------------------------
    // Utilitarios privados
    // -------------------------------------------------------------------------

    private void enviarPorCanal(LogEnvioNotificacao notif) {
        // Ponto de extensao: integrar com Firebase (PUSH), JavaMail (EMAIL), Twilio (SMS)
        switch (notif.getCanal()) {
            case EMAIL  -> log.debug("[EMAIL] To={} Subject={}", notif.getUsuario().getEmail(), notif.getTitulo());
            case PUSH   -> log.debug("[PUSH]  To={} Title={}",   notif.getUsuario().getId(), notif.getTitulo());
            case SMS    -> log.debug("[SMS]   To={} Msg={}",      notif.getUsuario().getId(), notif.getTitulo());
            case IN_APP -> log.debug("[IN_APP] userId={}",        notif.getUsuario().getId());
        }
    }

    private LogEnvioNotificacao criarSuprimido(
            Usuario usuario, LogEnvioNotificacao.TipoConteudo tipo,
            PreferenciaNotificacao.CanalEnvio canal, String titulo, String conteudo,
            String refTipo, Long refId, String motivo
    ) {
        LogEnvioNotificacao log = LogEnvioNotificacao.builder()
                .usuario(usuario)
                .tipoConteudo(tipo)
                .canal(canal)
                .titulo(titulo)
                .conteudo(conteudo)
                .statusEnvio(LogEnvioNotificacao.StatusEnvio.SUPRIMIDO)
                .motivoSupressao(motivo)
                .dataHoraEnvioPlanejado(LocalDateTime.now())
                .referenciaTipo(refTipo)
                .referenciaId(refId)
                .build();
        return logRepository.save(log);
    }
}
