package com.waldorf.application.service;

import com.waldorf.application.dto.NotificacaoDTO;
import com.waldorf.application.dto.PreferenciaNotificacaoDTO;
import com.waldorf.domain.entity.Notificacao;
import com.waldorf.domain.entity.PreferenciaNotificacao;
import com.waldorf.domain.entity.Usuario;
import com.waldorf.domain.enums.AgregacaoNotificacao;
import com.waldorf.domain.enums.TipoNotificacao;
import com.waldorf.infrastructure.repository.NotificacaoRepository;
import com.waldorf.infrastructure.repository.PreferenciaNotificacaoRepository;
import com.waldorf.infrastructure.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final PreferenciaNotificacaoRepository preferenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public List<NotificacaoDTO> listar(Long usuarioId) {
        return notificacaoRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId)
                .stream().map(this::toDTO).toList();
    }

    public long contarNaoLidas(Long usuarioId) {
        return notificacaoRepository.countByUsuarioIdAndLidaFalse(usuarioId);
    }

    @Transactional
    public void marcarComoLida(Long id, Long usuarioId) {
        Notificacao n = notificacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada: " + id));
        if (!n.getUsuario().getId().equals(usuarioId)) {
            throw new SecurityException("Acesso negado");
        }
        n.setLida(true);
        n.setLidaEm(LocalDateTime.now());
        notificacaoRepository.save(n);
    }

    @Transactional
    public void marcarTodasComoLidas(Long usuarioId) {
        notificacaoRepository.marcarTodasComoLidas(usuarioId);
    }

    @Transactional
    public NotificacaoDTO criar(Long usuarioId, TipoNotificacao tipo, String titulo, String mensagem, Long referenciaId, String referenciaTipo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + usuarioId));
        Notificacao n = Notificacao.builder()
                .usuario(usuario)
                .tipo(tipo)
                .titulo(titulo)
                .mensagem(mensagem)
                .referenciaId(referenciaId)
                .referenciaTipo(referenciaTipo)
                .lida(false)
                .build();
        return toDTO(notificacaoRepository.save(n));
    }

    public PreferenciaNotificacaoDTO buscarPreferencias(Long usuarioId) {
        return preferenciaRepository.findByUsuarioId(usuarioId)
                .map(this::toPrefDTO)
                .orElseGet(() -> new PreferenciaNotificacaoDTO(null, true, true, false, true, "IMEDIATO", null, null));
    }

    @Transactional
    public PreferenciaNotificacaoDTO salvarPreferencias(Long usuarioId, PreferenciaNotificacaoDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + usuarioId));
        PreferenciaNotificacao pref = preferenciaRepository.findByUsuarioId(usuarioId)
                .orElse(PreferenciaNotificacao.builder().usuario(usuario).build());
        pref.setEmail(dto.email());
        pref.setPush(dto.push());
        pref.setSms(dto.sms());
        pref.setInApp(dto.inApp());
        pref.setAgregacao(AgregacaoNotificacao.valueOf(dto.agregacao()));
        pref.setSilencioInicio(dto.silencioInicio() != null && !dto.silencioInicio().isBlank()
                ? java.time.LocalTime.parse(dto.silencioInicio()) : null);
        pref.setSilencioFim(dto.silencioFim() != null && !dto.silencioFim().isBlank()
                ? java.time.LocalTime.parse(dto.silencioFim()) : null);
        return toPrefDTO(preferenciaRepository.save(pref));
    }

    private NotificacaoDTO toDTO(Notificacao n) {
        return new NotificacaoDTO(
                n.getId(),
                n.getTipo().name(),
                n.getTitulo(),
                n.getMensagem(),
                n.getReferenciaId(),
                n.getReferenciaTipo(),
                n.isLida(),
                n.getLidaEm() != null ? n.getLidaEm().toString() : null,
                n.getCreatedAt().toString()
        );
    }

    private PreferenciaNotificacaoDTO toPrefDTO(PreferenciaNotificacao p) {
        return new PreferenciaNotificacaoDTO(
                p.getId(),
                p.isEmail(),
                p.isPush(),
                p.isSms(),
                p.isInApp(),
                p.getAgregacao().name(),
                p.getSilencioInicio() != null ? p.getSilencioInicio().toString() : null,
                p.getSilencioFim() != null ? p.getSilencioFim().toString() : null
        );
    }
}
