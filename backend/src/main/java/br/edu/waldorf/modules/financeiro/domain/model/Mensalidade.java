package br.edu.waldorf.modules.financeiro.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Mensalidade - parcela financeira mensal
 * Mapeia a tabela 'mensalidades'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(
    name = "mensalidades",
    indexes = {
        @Index(name = "idx_mensalidade_contrato_parcela", columnList = "contrato_id, numero_parcela"),
        @Index(name = "idx_mensalidade_vencimento",       columnList = "data_vencimento, status"),
        @Index(name = "idx_mensalidade_mes_ano",          columnList = "ano_referencia, mes_referencia")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensalidade extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contrato_id", nullable = false)
    private Contrato contrato;

    @NotNull
    @Column(name = "numero_parcela", nullable = false)
    private Integer numeroParcela;

    @NotNull
    @Column(name = "mes_referencia", nullable = false)
    private Integer mesReferencia;

    @NotNull
    @Column(name = "ano_referencia", nullable = false)
    private Integer anoReferencia;

    @NotNull
    @Column(name = "valor_parcela", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorParcela;

    @Column(name = "valor_desconto", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(name = "valor_juros", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorJuros = BigDecimal.ZERO;

    @Column(name = "valor_multa", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorMulta = BigDecimal.ZERO;

    @Column(name = "valor_pago", precision = 10, scale = 2)
    private BigDecimal valorPago;

    @NotNull
    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15)
    @Builder.Default
    private StatusMensalidade status = StatusMensalidade.ABERTA;

    @Column(name = "nosso_numero", length = 50)
    private String nossoNumero;

    @Column(name = "codigo_barras", length = 100)
    private String codigoBarras;

    @Column(name = "pix_qr_code", columnDefinition = "TEXT")
    private String pixQrCode;

    @Column(name = "gateway_transaction_id", length = 100)
    private String gatewayTransactionId;

    @OneToMany(mappedBy = "mensalidade", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pagamento> pagamentos = new ArrayList<>();

    // --- Métodos de negócio ---

    /** Valor total a pagar: parcela - desconto + juros + multa */
    public BigDecimal calcularValorTotal() {
        return valorParcela
                .subtract(valorDesconto != null ? valorDesconto : BigDecimal.ZERO)
                .add(valorJuros     != null ? valorJuros : BigDecimal.ZERO)
                .add(valorMulta     != null ? valorMulta : BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula juros simples de mora (1% a.m. / 0,033% a.d.)
     * e multa fixa de 2% após vencimento.
     */
    public void aplicarEncargosAtraso() {
        if (this.status != StatusMensalidade.ABERTA) return;
        long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(dataVencimento, LocalDate.now());
        if (diasAtraso <= 0) return;

        // Multa fixa 2%
        this.valorMulta = valorParcela
                .multiply(BigDecimal.valueOf(0.02))
                .setScale(2, RoundingMode.HALF_UP);
        // Juros 0,033% ao dia
        this.valorJuros = valorParcela
                .multiply(BigDecimal.valueOf(0.00033))
                .multiply(BigDecimal.valueOf(diasAtraso))
                .setScale(2, RoundingMode.HALF_UP);

        this.status = StatusMensalidade.ATRASADA;
    }

    public void registrarPagamento(BigDecimal valor) {
        this.valorPago = valor;
        this.dataPagamento = LocalDateTime.now();
        this.status = StatusMensalidade.PAGA;
    }

    public void cancelar() {
        this.status = StatusMensalidade.CANCELADA;
    }

    public boolean isEmAberto() {
        return this.status == StatusMensalidade.ABERTA || this.status == StatusMensalidade.ATRASADA;
    }

    // --- Enum ---

    public enum StatusMensalidade { ABERTA, PAGA, ATRASADA, CANCELADA, NEGOCIADA }
}
