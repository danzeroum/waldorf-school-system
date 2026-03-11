package br.edu.waldorf.modules.financeiro.domain.service;

import br.edu.waldorf.modules.financeiro.domain.model.Mensalidade;
import br.edu.waldorf.modules.financeiro.domain.model.Pagamento;
import br.edu.waldorf.modules.financeiro.domain.repository.MensalidadeRepository;
import br.edu.waldorf.modules.financeiro.domain.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de domínio para Mensalidade
 * Gerencia pagamentos, encargos e inadimplência
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MensalidadeService {

    private final MensalidadeRepository mensalidadeRepository;
    private final PagamentoRepository pagamentoRepository;

    @Transactional(readOnly = true)
    public Page<Mensalidade> listarComFiltros(
            Mensalidade.StatusMensalidade status, Integer anoRef, Integer mesRef, Pageable pageable
    ) {
        return mensalidadeRepository.findWithFilters(status, anoRef, mesRef, pageable);
    }

    @Transactional(readOnly = true)
    public Mensalidade buscarPorId(Long id) {
        return mensalidadeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mensalidade não encontrada: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<Mensalidade> listarPorContrato(Long contratoId) {
        return mensalidadeRepository.findByContratoIdOrderByNumeroParcela(contratoId);
    }

    @Transactional(readOnly = true)
    public List<Mensalidade> listarAtrasadas() {
        return mensalidadeRepository.findAtrasadas();
    }

    @Transactional(readOnly = true)
    public List<Mensalidade> listarAVencer(int diasAFrente) {
        LocalDate hoje = LocalDate.now();
        return mensalidadeRepository.findAVencerNoPeriodo(hoje, hoje.plusDays(diasAFrente));
    }

    @Transactional
    public Pagamento registrarPagamento(
            Long mensalidadeId,
            BigDecimal valor,
            Pagamento.FormaPagamento forma,
            String gatewayId,
            String comprovanteUrl
    ) {
        Mensalidade mensalidade = buscarPorId(mensalidadeId);

        if (!mensalidade.isEmAberto()) {
            throw new IllegalStateException("Mensalidade não está em aberto: status=" + mensalidade.getStatus());
        }

        Pagamento pagamento = Pagamento.builder()
                .mensalidade(mensalidade)
                .valorPago(valor)
                .dataPagamento(LocalDateTime.now())
                .formaPagamento(forma)
                .gatewayId(gatewayId)
                .comprovanteUrl(comprovanteUrl)
                .status(Pagamento.StatusPagamento.CONFIRMADO)
                .build();

        pagamentoRepository.save(pagamento);
        mensalidade.registrarPagamento(valor);
        mensalidadeRepository.save(mensalidade);

        log.info("Pagamento registrado: mensalidade={}, valor={}, forma={}",
                mensalidadeId, valor, forma);
        return pagamento;
    }

    @Transactional
    public Pagamento estornarPagamento(Long pagamentoId) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado: id=" + pagamentoId));
        pagamento.estornar();
        // Reabre a mensalidade
        Mensalidade mensalidade = pagamento.getMensalidade();
        mensalidade.setValorPago(null);
        mensalidade.setDataPagamento(null);
        mensalidade.setStatus(Mensalidade.StatusMensalidade.ABERTA);
        mensalidadeRepository.save(mensalidade);
        pagamentoRepository.save(pagamento);
        log.info("Pagamento estornado: id={}", pagamentoId);
        return pagamento;
    }

    /**
     * Job diário: aplica encargos em mensalidades vencidas.
     * Substitui o EVENT MySQL no ambiente Spring.
     */
    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void processarEncargosAtraso() {
        List<Mensalidade> abertas = mensalidadeRepository
                .findAVencerNoPeriodo(LocalDate.of(2000, 1, 1), LocalDate.now().minusDays(1));
        abertas.stream()
               .filter(m -> m.getStatus() == Mensalidade.StatusMensalidade.ABERTA)
               .forEach(m -> {
                   m.aplicarEncargosAtraso();
                   mensalidadeRepository.save(m);
               });
        log.info("Encargos de atraso processados: {} mensalidades", abertas.size());
    }
}
