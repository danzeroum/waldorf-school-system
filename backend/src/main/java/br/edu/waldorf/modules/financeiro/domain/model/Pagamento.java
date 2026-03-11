package br.edu.waldorf.modules.financeiro.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.security.domain.model.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade Pagamento - registro de pagamento de uma mensalidade
 * Mapeia a tabela 'pagamentos'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(
    name = "pagamentos",
    indexes = {
        @Index(name = "idx_pagamento_mensalidade", columnList = "mensalidade_id"),
        @Index(name = "idx_pagamento_data",        columnList = "data_pagamento"),
        @Index(name = "idx_pagamento_gateway",     columnList = "gateway_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pagamento extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mensalidade_id", nullable = false)
    private Mensalidade mensalidade;

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "valor_pago", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPago;

    @NotNull
    @Column(name = "data_pagamento", nullable = false)
    private LocalDateTime dataPagamento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 25)
    private FormaPagamento formaPagamento;

    @Column(name = "gateway_id", length = 100)
    private String gatewayId;

    @Column(name = "comprovante_url", length = 500)
    private String comprovanteUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15)
    @Builder.Default
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por")
    private Usuario registradoPor;

    // --- Métodos de negócio ---

    public void confirmar() {
        if (this.status != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Somente pagamentos PENDENTES podem ser confirmados");
        }
        this.status = StatusPagamento.CONFIRMADO;
    }

    public void estornar() {
        if (this.status != StatusPagamento.CONFIRMADO) {
            throw new IllegalStateException("Somente pagamentos CONFIRMADOS podem ser estornados");
        }
        this.status = StatusPagamento.ESTORNADO;
    }

    // --- Enums ---

    public enum FormaPagamento {
        BOLETO, PIX, CARTAO_CREDITO, DEBITO_AUTOMATICO, DINHEIRO, TRANSFERENCIA
    }

    public enum StatusPagamento { PENDENTE, CONFIRMADO, ESTORNADO, FALHA }
}
