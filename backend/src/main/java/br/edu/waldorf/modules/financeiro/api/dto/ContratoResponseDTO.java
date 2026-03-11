package br.edu.waldorf.modules.financeiro.api.dto;

import br.edu.waldorf.modules.financeiro.domain.model.Contrato;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta para Contrato
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratoResponseDTO {

    private Long id;
    private String numeroContrato;
    private Long alunoId;
    private String alunoNome;
    private Long responsavelId;
    private String responsavelNome;
    private Long planoId;
    private String planoNome;
    private Integer anoLetivo;
    private BigDecimal valorBase;
    private BigDecimal descontoTotal;
    private BigDecimal valorFinal;
    private LocalDate dataAssinatura;
    private LocalDate dataInicioVigencia;
    private LocalDate dataFimVigencia;
    private Contrato.SituacaoContrato situacao;
    private Contrato.FormaPagamento formaPagamento;
    private Integer totalParcelas;
    private Long parcelasPagas;
    private Long parcelasAtrasadas;
    private LocalDateTime createdAt;
}
