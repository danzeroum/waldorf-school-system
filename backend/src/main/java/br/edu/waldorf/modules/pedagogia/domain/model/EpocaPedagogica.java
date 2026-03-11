package br.edu.waldorf.modules.pedagogia.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.escolar.domain.model.Turma;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidade ÉpocaPedagógica - período temático Waldorf
 * Mapeia a tabela 'epocas_pedagogicas'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(
    name = "epocas_pedagogicas",
    indexes = {
        @Index(name = "idx_turma_status", columnList = "turma_id, status"),
        @Index(name = "idx_periodo",      columnList = "data_inicio, data_fim")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EpocaPedagogica extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @NotBlank
    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @NotNull
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @NotNull
    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "tema_central", columnDefinition = "TEXT")
    private String temaCentral;

    @Column(name = "narrativa_introdutoria", columnDefinition = "TEXT")
    private String narrativaIntrodutoria;

    @Column(name = "atividades_principais", columnDefinition = "TEXT")
    private String atividadesPrincipais;

    @Column(name = "materiais_necessarios", columnDefinition = "TEXT")
    private String materiaisNecessarios;

    @Column(name = "objetivos_desenvolvimento", columnDefinition = "TEXT")
    private String objetivosDesenvolvimento;

    @Column(name = "observacoes_diarias", columnDefinition = "TEXT")
    private String observacoesDiarias;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private StatusEpoca status = StatusEpoca.PLANEJADA;

    @Column(name = "cor_epoca", length = 7)
    @Builder.Default
    private String corEpoca = "#FF9800";

    // --- Métodos de negócio ---

    public void iniciar() {
        if (this.status != StatusEpoca.PLANEJADA) {
            throw new IllegalStateException("Somente épocas PLANEJADAS podem ser iniciadas");
        }
        this.status = StatusEpoca.EM_ANDAMENTO;
    }

    public void concluir() {
        this.status = StatusEpoca.CONCLUIDA;
    }

    public void cancelar() {
        this.status = StatusEpoca.CANCELADA;
    }

    public boolean isAtiva() {
        return this.status == StatusEpoca.EM_ANDAMENTO;
    }

    public boolean isNoPeriodo(LocalDate data) {
        return !data.isBefore(dataInicio) && !data.isAfter(dataFim);
    }

    // --- Enums ---

    public enum StatusEpoca {
        PLANEJADA,
        EM_ANDAMENTO,
        CONCLUIDA,
        CANCELADA
    }
}
