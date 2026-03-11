package br.edu.waldorf.modules.lgpd.domain.service;

import br.edu.waldorf.modules.lgpd.domain.model.ConsentimentoLgpd;
import br.edu.waldorf.modules.lgpd.domain.model.SolicitacaoTitular;
import br.edu.waldorf.modules.lgpd.domain.repository.ConsentimentoLgpdRepository;
import br.edu.waldorf.modules.lgpd.domain.repository.SolicitacaoTitularRepository;
import br.edu.waldorf.modules.security.domain.model.Usuario;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de dominio para LGPD
 * Gerencia consentimentos e solicitacoes de titulares
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LgpdService {

    private static final int PRAZO_DIAS = 15;

    private final ConsentimentoLgpdRepository consentimentoRepository;
    private final SolicitacaoTitularRepository solicitacaoRepository;

    // -------------------------------------------------------------------------
    // Consentimentos
    // -------------------------------------------------------------------------

    @Transactional
    public ConsentimentoLgpd registrarConsentimento(
            ConsentimentoLgpd consentimento, String ip, String versaoTermos, Usuario coletadoPor
    ) {
        consentimento.setIpConsentimento(ip);
        consentimento.setVersaoTermos(versaoTermos);
        consentimento.setColetadoPor(coletadoPor);
        consentimento.setDataConsentimento(LocalDateTime.now());
        ConsentimentoLgpd salvo = consentimentoRepository.save(consentimento);
        log.info("Consentimento registrado: pessoa={}, finalidade={}, consentido={}",
                salvo.getPessoa().getId(), salvo.getFinalidade(), salvo.getConsentido());
        return salvo;
    }

    @Transactional
    public ConsentimentoLgpd revogarConsentimento(Long consentimentoId) {
        ConsentimentoLgpd c = consentimentoRepository.findById(consentimentoId)
                .orElseThrow(() -> new EntityNotFoundException("Consentimento nao encontrado: " + consentimentoId));
        c.revogar();
        log.info("Consentimento revogado: id={}", consentimentoId);
        return consentimentoRepository.save(c);
    }

    @Transactional(readOnly = true)
    public List<ConsentimentoLgpd> listarConsentimentosAtivos(Long pessoaId) {
        return consentimentoRepository.findAtivosbyPessoa(pessoaId);
    }

    // -------------------------------------------------------------------------
    // Solicitacoes de titulares
    // -------------------------------------------------------------------------

    @Transactional
    public SolicitacaoTitular abrirSolicitacao(SolicitacaoTitular solicitacao) {
        solicitacao.setDataSolicitacao(LocalDateTime.now());
        // Prazo legal: 15 dias corridos a partir da abertura
        solicitacao.setPrazoResposta(LocalDate.now().plusDays(PRAZO_DIAS));
        solicitacao.setStatus(SolicitacaoTitular.StatusSolicitacao.ABERTA);
        SolicitacaoTitular salva = solicitacaoRepository.save(solicitacao);
        log.info("Solicitacao LGPD aberta: id={}, tipo={}, pessoa={}",
                salva.getId(), salva.getTipoSolicitacao(), salva.getPessoa().getId());
        return salva;
    }

    @Transactional
    public SolicitacaoTitular avancarStatus(Long id) {
        SolicitacaoTitular s = buscarPorId(id);
        switch (s.getStatus()) {
            case ABERTA         -> s.iniciarAnalise();
            case EM_ANALISE     -> s.iniciarAtendimento();
            default -> throw new IllegalStateException("Status nao permite avanco: " + s.getStatus());
        }
        return solicitacaoRepository.save(s);
    }

    @Transactional
    public SolicitacaoTitular concluir(Long id, String resposta, Usuario atendente) {
        SolicitacaoTitular s = buscarPorId(id);
        s.concluir(resposta, atendente);
        log.info("Solicitacao LGPD concluida: id={}", id);
        return solicitacaoRepository.save(s);
    }

    @Transactional
    public SolicitacaoTitular rejeitar(Long id, String justificativa, Usuario atendente) {
        SolicitacaoTitular s = buscarPorId(id);
        s.rejeitar(justificativa, atendente);
        log.info("Solicitacao LGPD rejeitada: id={}", id);
        return solicitacaoRepository.save(s);
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoTitular> listarEmAberto() {
        return solicitacaoRepository.findEmAberto();
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoTitular> listarPorPessoa(Long pessoaId, Pageable pageable) {
        return solicitacaoRepository.findByPessoaIdOrderByDataSolicitacaoDesc(pessoaId, pageable);
    }

    @Transactional(readOnly = true)
    public SolicitacaoTitular buscarPorId(Long id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitacao LGPD nao encontrada: " + id));
    }

    // -------------------------------------------------------------------------
    // Job diario: alerta solicitacoes atrasadas
    // -------------------------------------------------------------------------

    @Scheduled(cron = "0 0 8 * * *")
    @Transactional(readOnly = true)
    public void alertarSolicitacoesAtrasadas() {
        List<SolicitacaoTitular> atrasadas = solicitacaoRepository.findAtrasadas(LocalDate.now());
        if (!atrasadas.isEmpty()) {
            log.warn("LGPD ALERTA: {} solicitacoes com prazo expirado!", atrasadas.size());
            atrasadas.forEach(s ->
                log.warn("  -> id={}, tipo={}, prazo={}, pessoa={}",
                        s.getId(), s.getTipoSolicitacao(), s.getPrazoResposta(), s.getPessoa().getId())
            );
        }
    }
}
