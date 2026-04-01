package com.waldorf.application.service;

import com.waldorf.application.dto.financeiro.BaixaPagamentoRequestDTO;
import com.waldorf.application.dto.financeiro.MensalidadeResponseDTO;
import com.waldorf.domain.entity.Contrato;
import com.waldorf.domain.entity.Mensalidade;
import com.waldorf.domain.entity.Mensalidade.StatusMensalidade;
import com.waldorf.domain.repository.ContratoRepository;
import com.waldorf.domain.repository.MensalidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class MensalidadeService {

    private final MensalidadeRepository mensalidadeRepository;
    private final ContratoRepository contratoRepository;

    public MensalidadeService(MensalidadeRepository mensalidadeRepository,
                              ContratoRepository contratoRepository) {
        this.mensalidadeRepository = mensalidadeRepository;
        this.contratoRepository = contratoRepository;
    }

    /**
     * Gera as mensalidades de um contrato ao ativar.
     * Chamado por ContratoService.ativar().
     */
    public void gerarMensalidades(Contrato contrato) {
        int total = contrato.getTotalParcelas();
        BigDecimal valor = contrato.getValorMensalidade()
                .divide(BigDecimal.ONE, 2, RoundingMode.HALF_UP);
        LocalDate inicio = contrato.getDataInicio();
        int diaVenc = contrato.getDiaVencimento();

        for (int i = 1; i <= total; i++) {
            LocalDate vencimento = inicio.plusMonths(i - 1)
                    .withDayOfMonth(Math.min(diaVenc,
                            inicio.plusMonths(i - 1).lengthOfMonth()));

            Mensalidade m = new Mensalidade();
            m.setContrato(contrato);
            m.setNumero(i);
            m.setDescricao("Mensalidade " + i + "/" + total + " — "
                    + vencimento.format(DateTimeFormatter.ofPattern("MMM/yyyy")));
            m.setValor(valor);
            m.setDataVencimento(vencimento);
            m.setStatus(StatusMensalidade.PENDENTE);
            mensalidadeRepository.save(m);
        }
    }

    @Transactional(readOnly = true)
    public List<MensalidadeResponseDTO> listarPorContrato(Long contratoId) {
        return mensalidadeRepository.findByContratoIdOrderByNumeroAsc(contratoId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<MensalidadeResponseDTO> listarPorStatus(StatusMensalidade status) {
        return mensalidadeRepository.findByStatus(status)
                .stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<MensalidadeResponseDTO> listarVencidas() {
        return mensalidadeRepository
                .findByDataVencimentoBeforeAndStatus(LocalDate.now(), StatusMensalidade.PENDENTE)
                .stream().map(this::toDTO).toList();
    }

    public MensalidadeResponseDTO registrarPagamento(Long id, BaixaPagamentoRequestDTO dto) {
        Mensalidade m = mensalidadeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mensalidade não encontrada: " + id));

        m.setValorPago(dto.valorPago());
        m.setDataPagamento(dto.dataPagamento());
        m.setFormaPagamento(dto.formaPagamento());
        m.setObservacao(dto.observacao());

        // Determina status pelo valor pago
        int cmp = dto.valorPago().compareTo(m.getValor());
        if (cmp >= 0) {
            m.setStatus(StatusMensalidade.PAGA);
        } else {
            m.setStatus(StatusMensalidade.PARCIAL);
        }

        return toDTO(mensalidadeRepository.save(m));
    }

    // --- helpers ---

    private MensalidadeResponseDTO toDTO(Mensalidade m) {
        return new MensalidadeResponseDTO(
                m.getId(),
                m.getContrato().getId(),
                m.getContrato().getAluno() != null ? m.getContrato().getAluno().getNome() : null,
                m.getNumero(),
                m.getDescricao(),
                m.getValor(),
                m.getValorPago(),
                m.getDataVencimento(),
                m.getDataPagamento(),
                m.getStatus(),
                m.getFormaPagamento(),
                m.getObservacao(),
                m.getCreatedAt()
        );
    }
}
