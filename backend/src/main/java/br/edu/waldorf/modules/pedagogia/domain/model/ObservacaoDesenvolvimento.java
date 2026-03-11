package br.edu.waldorf.modules.pedagogia.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.escolar.domain.model.Turma;
import br.edu.waldorf.modules.pessoa.domain.model.Aluno;
import br.edu.waldorf.modules.pessoa.domain.model.Professor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidade ObservaçãoDesenvolvimento - registro pedagógico individual
 * Mapeia a tabela 'observacoes_desenvolvimento'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(
    name = "observacoes_desenvolvimento",
    indexes = {
        @Index(name = "idx_aluno_turma_data", columnList = "aluno_id, turma_id, data_observacao"),
        @Index(name = "idx_professor",        columnList = "professor_id, data_observacao")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacaoDesenvolvimento extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epoca_id")
    private EpocaPedagogica epoca;

    @NotNull
    @Column(name = "data_observacao", nullable = false)
    private LocalDate dataObservacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "aspecto", length = 20)
    private AspectoDensenvolvimento aspecto;

    @Column(name = "observacao_temperamento", length = 100)
    private String observacaoTemperamento;

    @Column(name = "titulo", length = 200)
    private String titulo;

    @NotBlank
    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "evidencias", columnDefinition = "TEXT")
    private String evidencias;

    @Column(name = "sugestoes_apoio", columnDefinition = "TEXT")
    private String sugestoesApoio;

    @Column(name = "privado")
    @Builder.Default
    private Boolean privado = false;

    @Column(name = "compartilhar_pais")
    @Builder.Default
    private Boolean compartilharPais = true;

    // --- Enums ---

    public enum AspectoDensenvolvimento {
        FISICO,
        ANIMICO,
        COGNITIVO,
        SOCIAL,
        ARTISTICO,
        MANUAL,
        LINGUAGEM,
        NATUREZA,
        OUTRO
    }
}
