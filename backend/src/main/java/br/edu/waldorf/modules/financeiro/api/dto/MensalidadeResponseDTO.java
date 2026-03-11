package br.edu.waldorf.modules.financeiro.api.dto;

import br.edu.waldorf.modules.financeiro.domain.model.Mensalidade;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta para Mensalidade
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensalidadeResponseDTO {

    private Long id;
    private Long contratoId;
    private String alunoNome;
    private Integer numeroParcela;
    private Integer mesReferencia;
    private Integer anoReferencia;
    private BigDecimal valorParcela;
    private BigDecimal valorDesconto;
    private BigDecimal valorJuros;
    private BigDecimal valorMulta;
    private BigDecimal valorTotal;
    private BigDecimal valorPago;
    private LocalDate dataVencimento;
    private LocalDateTime dataPagamento;
    private Mensalidade.StatusMensalidade status;
    private String codigoBarras;
    private String pixQrCode;
}
