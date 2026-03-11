package br.edu.waldorf.modules.pedagogia.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.escolar.domain.model.Turma;
import br.edu.waldorf.modules.pessoa.domain.model.Professor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

/**
 * Entidade RitmoDiárioSemanal - estrutura do ritmo da turma Waldorf
 * Mapeia a tabela 'ritmo_diario_semanal'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(
    name = "ritmo_diario_semanal",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_turma_dia_momento",
        columnNames = {"turma_id", "dia_semana", "momento"}
    ),
    indexes = {
        @Index(name = "idx_turma_dia", columnList = "turma_id, dia_semana")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RitmoDiarioSemanal extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 10)
    private DiaSemana diaSemana;

    @NotBlank
    @Column(name = "momento", nullable = false, length = 100)
    private String momento;

    @NotBlank
    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @NotNull
    @Column(name = "horario_inicio", nullable = false)
    private LocalTime horarioInicio;

    @NotNull
    @Column(name = "horario_fim", nullable = false)
    private LocalTime horarioFim;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_atividade", length = 30)
    private TipoAtividade tipoAtividade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private Professor responsavel;

    @Column(name = "local", length = 100)
    private String local;

    @Column(name = "ativo")
    @Builder.Default
    private Boolean ativo = true;

    // --- Métodos utilitários ---

    public int duracaoMinutos() {
        return (int) java.time.Duration.between(horarioInicio, horarioFim).toMinutes();
    }

    // --- Enums ---

    public enum DiaSemana {
        SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO
    }

    public enum TipoAtividade {
        VERSO,
        CIRCULO_RITMICO,
        ATIVIDADE_PRINCIPAL,
        TRABALHO_MANUAL,
        BRINCAR_LIVRE,
        ALIMENTACAO,
        DESPEDIDA
    }
}
