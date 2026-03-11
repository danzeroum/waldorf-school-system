package br.edu.waldorf.modules.financeiro.api.dto;

import br.edu.waldorf.modules.financeiro.domain.model.Pagamento;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO de requisição para registrar pagamento
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagamentoRequestDTO {

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Forma de pagamento é obrigatória")
    private Pagamento.FormaPagamento formaPagamento;

    private String gatewayId;
    private String comprovanteUrl;
    private String observacoes;
}
