package com.waldorf.application.service;

import com.waldorf.domain.entity.Contrato;
import com.waldorf.domain.entity.Mensalidade;
import com.waldorf.infrastructure.repository.MensalidadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MensalidadeService {

    private final MensalidadeRepository mensalidadeRepository;

    @Transactional
    public void gerarMensalidades(Contrato contrato) {
        log.info("Gerando {} mensalidades para contrato {}", contrato.getTotalParcelas(), contrato.getId());

        LocalDate dataInicio = contrato.getDataInicio() != null ? contrato.getDataInicio() : LocalDate.now();
        int diaVencimento = contrato.getDiaVencimento() > 0 ? contrato.getDiaVencimento() : 10;
        int totalParcelas = contrato.getTotalParcelas() > 0 ? contrato.getTotalParcelas() : 12;

        for (int i = 1; i <= totalParcelas; i++) {
            LocalDate vencimento = calcularVencimento(dataInicio, diaVencimento, i);

            Mensalidade m = Mensalidade.builder()
                    .contrato(contrato)
                    .numeroParcela(i)
                    .mesReferencia(vencimento.getMonthValue())
                    .anoReferencia(vencimento.getYear())
                    .valorParcela(contrato.getValorMensalidade())
                    .valorDesconto(contrato.getDesconto() != null ? contrato.getDesconto() : BigDecimal.ZERO)
                    .valorJuros(BigDecimal.ZERO)
                    .valorMulta(BigDecimal.ZERO)
                    .dataVencimento(vencimento)
                    .status(Mensalidade.StatusMensalidade.ABERTA)
                    .build();

            mensalidadeRepository.save(m);
        }

        log.info("Mensalidades geradas com sucesso para contrato {}", contrato.getId());
    }

    private LocalDate calcularVencimento(LocalDate dataInicio, int diaVencimento, int parcela) {
        int mes = dataInicio.getMonthValue() + parcela - 1;
        int ano = dataInicio.getYear() + (mes - 1) / 12;
        mes = ((mes - 1) % 12) + 1;

        int ultimoDia = LocalDate.of(ano, mes, 1).lengthOfMonth();
        int dia = Math.min(diaVencimento, ultimoDia);

        return LocalDate.of(ano, mes, dia);
    }

    public List<Mensalidade> listarPorContrato(Long contratoId) {
        return mensalidadeRepository.findByContratoIdOrderByNumeroParcela(contratoId);
    }
}
