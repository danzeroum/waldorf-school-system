package br.edu.waldorf.modules.escolar.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.pessoa.domain.model.Professor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

/**
 * Entidade TurmaDisciplina - associação Turma x Disciplina x Professor
 * Mapeia a tabela 'turma_disciplinas'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(
    name = "turma_disciplinas",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_turma_disciplina",
        columnNames = {"turma_id", "disciplina_id"}
    ),
    indexes = {
        @Index(name = "idx_turma_disc_professor", columnList = "professor_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurmaDisciplina extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @Column(name = "carga_horaria_semanal")
    private Integer cargaHorariaSemanal;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", length = 10)
    private DiaSemana diaSemana;

    @Column(name = "horario_inicio")
    private LocalTime horarioInicio;

    @Column(name = "horario_fim")
    private LocalTime horarioFim;

    public enum DiaSemana {
        SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO
    }
}
