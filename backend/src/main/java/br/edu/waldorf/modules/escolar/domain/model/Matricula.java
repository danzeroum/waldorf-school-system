package br.edu.waldorf.modules.escolar.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.pessoa.domain.model.Aluno;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade Matrícula - vínculo Aluno x Turma
 * Mapeia a tabela 'matriculas'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(
    name = "matriculas",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_aluno_turma_ano",
        columnNames = {"aluno_id", "turma_id", "ano_letivo"}
    ),
    indexes = {
        @Index(name = "idx_turma_ano",       columnList = "turma_id, ano_letivo"),
        @Index(name = "idx_aluno_situacao",   columnList = "aluno_id, situacao")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Matricula extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @Column(name = "numero_matricula", unique = true, length = 30)
    private String numeroMatricula;

    @NotNull
    @Column(name = "ano_letivo", nullable = false)
    private Integer anoLetivo;

    @NotNull
    @Column(name = "data_matricula", nullable = false)
    private LocalDate dataMatricula;

    @Column(name = "data_cancelamento")
    private LocalDate dataCancelamento;

    @Column(name = "motivo_cancelamento", columnDefinition = "TEXT")
    private String motivoCancelamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_ingresso", length = 20)
    private FormaIngresso formaIngresso;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ensino", length = 20)
    private TipoEnsino tipoEnsino;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", length = 20)
    @Builder.Default
    private SituacaoMatricula situacao = SituacaoMatricula.EM_ANDAMENTO;

    @Column(name = "media_final", precision = 5, scale = 2)
    private BigDecimal mediaFinal;

    @Column(name = "frequencia_final", precision = 5, scale = 2)
    private BigDecimal frequenciaFinal;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    // --- Métodos de negócio ---

    public void cancelar(String motivo) {
        this.situacao = SituacaoMatricula.CANCELADA;
        this.dataCancelamento = LocalDate.now();
        this.motivoCancelamento = motivo;
    }

    public void transferir() {
        this.situacao = SituacaoMatricula.TRANSFERIDA;
        this.dataCancelamento = LocalDate.now();
    }

    public boolean isAtiva() {
        return this.situacao == SituacaoMatricula.EM_ANDAMENTO
            || this.situacao == SituacaoMatricula.ATIVA;
    }

    // --- Enums ---

    public enum SituacaoMatricula {
        ATIVA,
        TRANCADA,
        CANCELADA,
        TRANSFERIDA,
        APROVADA,
        REPROVADA,
        EM_ANDAMENTO
    }

    public enum FormaIngresso {
        NOVA,
        RENOVACAO,
        TRANSFERENCIA
    }

    public enum TipoEnsino {
        REGULAR,
        EJA,
        SUPLETIVO
    }
}
