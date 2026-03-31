package com.waldorf.application.service;

import com.waldorf.application.dto.ConsentimentoDTO;
import com.waldorf.application.dto.ResumoLgpdDTO;
import com.waldorf.application.dto.SolicitacaoDTO;
import com.waldorf.application.dto.ResponderSolicitacaoRequest;
import com.waldorf.domain.entity.ConsentimentoLgpd;
import com.waldorf.domain.entity.SolicitacaoTitular;
import com.waldorf.domain.enums.StatusConsentimento;
import com.waldorf.domain.enums.StatusSolicitacao;
import com.waldorf.infrastructure.repository.ConsentimentoLgpdRepository;
import com.waldorf.infrastructure.repository.SolicitacaoTitularRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LgpdService {

    private final SolicitacaoTitularRepository solicitacaoRepository;
    private final ConsentimentoLgpdRepository consentimentoRepository;

    // ── Consentimentos ────────────────────────────────────────────────────────

    public List<ConsentimentoDTO> listarConsentimentos(String status) {
        List<ConsentimentoLgpd> lista = status != null && !status.isBlank()
                ? consentimentoRepository.findByStatusOrderByCreatedAtDesc(StatusConsentimento.valueOf(status))
                : consentimentoRepository.findByOrderByCreatedAtDesc();
        return lista.stream().map(this::toConsentimentoDTO).toList();
    }

    // ── Solicitações ──────────────────────────────────────────────────────────

    public List<SolicitacaoDTO> listarSolicitacoes(String status) {
        List<SolicitacaoTitular> lista = status != null && !status.isBlank()
                ? solicitacaoRepository.findAll().stream()
                        .filter(s -> s.getStatus().name().equals(status)).toList()
                : solicitacaoRepository.findAll();
        return lista.stream().map(this::toSolicitacaoDTO).toList();
    }

    @Transactional
    public SolicitacaoDTO responder(Long id, ResponderSolicitacaoRequest req) {
        SolicitacaoTitular s = findOrThrow(id);
        s.setStatus(StatusSolicitacao.valueOf(req.novoStatus()));
        s.setResposta(req.resposta());
        return toSolicitacaoDTO(solicitacaoRepository.save(s));
    }

    // ── Resumo ────────────────────────────────────────────────────────────────

    public ResumoLgpdDTO resumo() {
        long total        = consentimentoRepository.count();
        long ativos       = consentimentoRepository.countAtivos();
        long pendentes    = consentimentoRepository.countByStatus(StatusConsentimento.PENDENTE);
        long revogados    = consentimentoRepository.countByStatus(StatusConsentimento.REVOGADO);
        // FIX: StatusSolicitacao não tem PENDENTE → usa ABERTA
        long solPendentes = solicitacaoRepository.findAll().stream()
                .filter(s -> s.getStatus() == StatusSolicitacao.ABERTA).count();
        long solAnalise   = solicitacaoRepository.findAll().stream()
                .filter(s -> s.getStatus() == StatusSolicitacao.EM_ANALISE).count();
        int conformidade  = total > 0 ? (int) ((ativos * 100) / total) : 100;
        return new ResumoLgpdDTO(total, ativos, pendentes, revogados, solPendentes, solAnalise, conformidade);
    }

    // ── Legado (mantido por compatibilidade) ──────────────────────────────────

    @Transactional
    public void avancarStatus(Long id) {
        SolicitacaoTitular s = findOrThrow(id);
        s.setStatus(StatusSolicitacao.EM_ANALISE);
        solicitacaoRepository.save(s);
    }

    @Transactional
    public void concluir(Long id, String resposta) {
        SolicitacaoTitular s = findOrThrow(id);
        s.setStatus(StatusSolicitacao.CONCLUIDA);
        s.setResposta(resposta);
        solicitacaoRepository.save(s);
    }

    @Transactional
    public void rejeitar(Long id, String motivo) {
        SolicitacaoTitular s = findOrThrow(id);
        s.setStatus(StatusSolicitacao.REJEITADA);
        s.setResposta(motivo);
        solicitacaoRepository.save(s);
    }

    public LocalDate calcularPrazo() { return LocalDate.now().plusDays(15); }

    private SolicitacaoTitular findOrThrow(Long id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada: " + id));
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private ConsentimentoDTO toConsentimentoDTO(ConsentimentoLgpd c) {
        return new ConsentimentoDTO(
                c.getId(),
                c.getAluno().getId(),
                c.getAluno().getNome(),
                c.getResponsavel().getNome(),
                c.getResponsavel().getEmail(),
                c.getTipo().name(),
                c.getStatus().name(),
                c.getDataAceite() != null ? c.getDataAceite().toString() : null,
                c.getDataRevogacao() != null ? c.getDataRevogacao().toString() : null,
                c.getVersaoTermos()
        );
    }

    private SolicitacaoDTO toSolicitacaoDTO(SolicitacaoTitular s) {
        return new SolicitacaoDTO(
                s.getId(),
                s.getTipo().name(),
                s.getStatus().name(),
                s.getDescricao(),
                s.getResposta(),
                s.getPrazo() != null ? s.getPrazo().toString() : null,
                s.getCreatedAt() != null ? s.getCreatedAt().toString() : null
        );
    }
}
