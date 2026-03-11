package br.edu.waldorf.modules.financeiro.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entidade PlanoMensalidade - plano financeiro por ano de vigência
 * Mapeia a tabela 'planos_mensalidade'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(
    name = "planos_mensalidade",
    indexes = @Index(name = "idx_ano_ativo", columnList = "ano_vigencia, ativo")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanoMensalidade extends BaseEntity {

    @NotBlank
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "valor_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorBase;

    @Column(name = "numero_parcelas")
    @Builder.Default
    private Integer numeroParcelas = 12;

    @Column(name = "desconto_anual_percentual", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal descontoAnualPercentual = BigDecimal.ZERO;

    @Column(name = "desconto_irmao_percentual", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal descontoIrmaoPercentual = BigDecimal.ZERO;

    @Column(name = "taxa_matricula", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxaMatricula = BigDecimal.ZERO;

    @Column(name = "ativo")
    @Builder.Default
    private Boolean ativo = true;

    @NotNull
    @Column(name = "ano_vigencia", nullable = false)
    private Integer anoVigencia;

    // --- Métodos utilitários ---

    public BigDecimal calcularValorComDescontoAnual() {
        if (descontoAnualPercentual == null || descontoAnualPercentual.compareTo(BigDecimal.ZERO) == 0) {
            return valorBase;
        }
        BigDecimal fator = BigDecimal.ONE.subtract(
                descontoAnualPercentual.divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP)
        );
        return valorBase.multiply(fator).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal calcularValorComDescontoIrmao() {
        if (descontoIrmaoPercentual == null || descontoIrmaoPercentual.compareTo(BigDecimal.ZERO) == 0) {
            return valorBase;
        }
        BigDecimal fator = BigDecimal.ONE.subtract(
                descontoIrmaoPercentual.divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP)
        );
        return valorBase.multiply(fator).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
