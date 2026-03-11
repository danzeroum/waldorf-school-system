package br.edu.waldorf.modules.financeiro.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.pessoa.domain.model.Aluno;
import br.edu.waldorf.modules.pessoa.domain.model.Responsavel;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entidade Contrato - contrato financeiro anual escola/família
 * Mapeia a tabela 'contratos'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(
    name = "contratos",
    indexes = {
        @Index(name = "idx_contrato_aluno",    columnList = "aluno_id"),
        @Index(name = "idx_contrato_situacao", columnList = "situacao, ano_letivo"),
        @Index(name = "idx_contrato_numero",   columnList = "numero_contrato")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contrato extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsavel_id", nullable = false)
    private Responsavel responsavel;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plano_id", nullable = false)
    private PlanoMensalidade plano;

    @NotBlank
    @Column(name = "numero_contrato", nullable = false, unique = true, length = 30)
    private String numeroContrato;

    @NotNull
    @Column(name = "ano_letivo", nullable = false)
    private Integer anoLetivo;

    @NotNull
    @Column(name = "valor_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorBase;

    @Column(name = "desconto_total", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal descontoTotal = BigDecimal.ZERO;

    @NotNull
    @Column(name = "valor_final", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorFinal;

    @Column(name = "data_assinatura")
    private LocalDate dataAssinatura;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @NotNull
    @Column(name = "data_inicio_vigencia", nullable = false)
    private LocalDate dataInicioVigencia;

    @NotNull
    @Column(name = "data_fim_vigencia", nullable = false)
    private LocalDate dataFimVigencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", length = 15)
    @Builder.Default
    private SituacaoContrato situacao = SituacaoContrato.PENDENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", length = 20)
    @Builder.Default
    private FormaPagamento formaPagamento = FormaPagamento.BOLETO;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "termos_especiais", columnDefinition = "JSON")
    private Map<String, Object> termosEspeciais;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Mensalidade> mensalidades = new ArrayList<>();

    // --- Métodos de negócio ---

    public void ativar() {
        if (this.situacao != SituacaoContrato.PENDENTE) {
            throw new IllegalStateException("Somente contratos PENDENTES podem ser ativados");
        }
        this.situacao = SituacaoContrato.ATIVO;
        this.dataAssinatura = LocalDate.now();
    }

    public void suspender() {
        if (this.situacao != SituacaoContrato.ATIVO) {
            throw new IllegalStateException("Somente contratos ATIVOS podem ser suspensos");
        }
        this.situacao = SituacaoContrato.SUSPENSO;
    }

    public void reativar() {
        if (this.situacao != SituacaoContrato.SUSPENSO) {
            throw new IllegalStateException("Somente contratos SUSPENSOS podem ser reativados");
        }
        this.situacao = SituacaoContrato.ATIVO;
    }

    public void cancelar() {
        if (this.situacao == SituacaoContrato.ENCERRADO) {
            throw new IllegalStateException("Contrato já encerrado");
        }
        this.situacao = SituacaoContrato.CANCELADO;
    }

    public void encerrar() {
        this.situacao = SituacaoContrato.ENCERRADO;
    }

    public boolean isAtivo() {
        return this.situacao == SituacaoContrato.ATIVO;
    }

    // --- Enums ---

    public enum SituacaoContrato { PENDENTE, ATIVO, SUSPENSO, CANCELADO, ENCERRADO }

    public enum FormaPagamento { BOLETO, PIX, CARTAO_CREDITO, DEBITO_AUTOMATICO }
}
