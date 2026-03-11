package br.edu.waldorf.modules.pessoa.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Professor - especialização de Pessoa
 * Mapeia a tabela 'professores' com herança JOINED de 'pessoas'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(name = "professores", indexes = {
    @Index(name = "idx_professor_registro",   columnList = "registro_profissional"),
    @Index(name = "idx_professor_situacao",   columnList = "situacao")
})
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Professor extends Pessoa {

    @Column(name = "registro_profissional", unique = true, length = 30)
    private String registroProfissional;

    @Column(name = "formacao", length = 200)
    private String formacao;

    @Column(name = "especializacao_waldorf", length = 200)
    private String especializacaoWaldorf;

    @Column(name = "ano_formacao_waldorf")
    private Integer anoFormacaoWaldorf;

    @Column(name = "biografia", columnDefinition = "TEXT")
    private String biografia;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", length = 20)
    @Builder.Default
    private SituacaoProfessor situacao = SituacaoProfessor.ATIVO;

    @Column(name = "data_admissao")
    private java.time.LocalDate dataAdmissao;

    @Column(name = "data_demissao")
    private java.time.LocalDate dataDemissao;

    // --- Relacionamentos ---
    @OneToMany(mappedBy = "professorTitular", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Turma> turmasTitular = new ArrayList<>();

    // --- Enums ---

    public enum SituacaoProfessor {
        ATIVO,
        INATIVO,
        AFASTADO,
        DESLIGADO
    }
}
