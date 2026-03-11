package br.edu.waldorf.modules.escolar.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.pessoa.domain.model.Professor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Turma
 * Mapeia a tabela 'turmas'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(name = "turmas", indexes = {
    @Index(name = "idx_ano_curso",    columnList = "ano_letivo, curso_id"),
    @Index(name = "idx_professor",    columnList = "professor_titular_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Turma extends BaseEntity {

    @NotBlank
    @Column(name = "codigo", unique = true, nullable = false, length = 20)
    private String codigo;

    @NotBlank
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @NotNull
    @Column(name = "ano_letivo", nullable = false)
    private Integer anoLetivo;

    @NotNull
    @Column(name = "serie", nullable = false)
    private Integer serie;

    @Enumerated(EnumType.STRING)
    @Column(name = "turno", length = 15)
    private Turno turno;

    @Column(name = "sala", length = 50)
    private String sala;

    @Column(name = "capacidade_maxima")
    @Builder.Default
    private Integer capacidadeMaxima = 25;

    @Column(name = "vagas_disponiveis")
    private Integer vagasDisponiveis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_titular_id")
    private Professor professorTitular;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_auxiliar_id")
    private Professor professorAuxiliar;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", length = 20)
    @Builder.Default
    private SituacaoTurma situacao = SituacaoTurma.ABERTA;

    @Column(name = "cor_turma", length = 7)
    @Builder.Default
    private String corTurma = "#2196F3";

    // --- Relacionamentos ---
    @OneToMany(mappedBy = "turma", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TurmaDisciplina> disciplinas = new ArrayList<>();

    // --- Métodos de negócio ---

    public boolean possuiVagas() {
        return vagasDisponiveis != null && vagasDisponiveis > 0;
    }

    public void iniciar() {
        this.situacao = SituacaoTurma.EM_ANDAMENTO;
    }

    public void concluir() {
        this.situacao = SituacaoTurma.CONCLUIDA;
    }

    public void cancelar() {
        this.situacao = SituacaoTurma.CANCELADA;
    }

    // --- Enums ---

    public enum SituacaoTurma {
        ABERTA,
        EM_ANDAMENTO,
        CONCLUIDA,
        CANCELADA
    }

    public enum Turno {
        MATUTINO,
        VESPERTINO,
        NOTURNO,
        INTEGRAL
    }
}
