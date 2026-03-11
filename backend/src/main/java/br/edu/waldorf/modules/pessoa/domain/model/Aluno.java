package br.edu.waldorf.modules.pessoa.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Aluno - especialização de Pessoa
 * Mapeia a tabela 'alunos' com herança JOINED de 'pessoas'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(name = "alunos", indexes = {
    @Index(name = "idx_aluno_matricula",      columnList = "numero_matricula"),
    @Index(name = "idx_aluno_turma",          columnList = "turma_id"),
    @Index(name = "idx_aluno_situacao",       columnList = "situacao")
})
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aluno extends Pessoa {

    @Column(name = "numero_matricula", unique = true, length = 20)
    private String numeroMatricula;

    @Column(name = "nome_social", length = 150)
    private String nomeSocial;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", length = 20)
    @Builder.Default
    private SituacaoAluno situacao = SituacaoAluno.ATIVO;

    // --- Dados Médicos ---
    @Column(name = "tipo_sanguineo", length = 5)
    private String tipoSanguineo;

    @Column(name = "plano_saude", length = 100)
    private String planoSaude;

    @Column(name = "numero_carteirinha_saude", length = 50)
    private String numeroCarteirinhaSaude;

    @Column(name = "alergias", columnDefinition = "TEXT")
    private String alergias;

    @Column(name = "medicamentos_controlados", columnDefinition = "TEXT")
    private String medicamentosControlados;

    @Column(name = "necessidades_especiais", columnDefinition = "TEXT")
    private String necessidadesEspeciais;

    @Column(name = "observacoes_medicas", columnDefinition = "TEXT")
    private String observacoesMedicas;

    // --- Dados Waldorf ---
    @Column(name = "temperamento", length = 20)
    private String temperamento; // SANGUINEO, COLERICO, MELANCOLICO, FLEUMATICO

    @Column(name = "ano_ingresso")
    private Integer anoIngresso;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_ingresso", length = 20)
    private FormaIngresso formaIngresso;

    @Column(name = "naturalidade", length = 100)
    private String naturalidade;

    @Column(name = "nacionalidade", length = 50)
    @Builder.Default
    private String nacionalidade = "Brasileiro";

    @Column(name = "nome_pai", length = 200)
    private String nomePai;

    @Column(name = "nome_mae", length = 200)
    private String nomeMae;

    // --- Relacionamentos ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id")
    private Turma turmaAtual;

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ResponsavelAluno> responsaveis = new ArrayList<>();

    // --- Métodos de negócio ---

    public void transferir(Turma novaTurma) {
        this.turmaAtual = novaTurma;
    }

    public void desligar() {
        this.situacao = SituacaoAluno.DESLIGADO;
        this.inativar();
    }

    // --- Enums ---

    public enum SituacaoAluno {
        ATIVO,
        INATIVO,
        TRANCADO,
        DESLIGADO,
        CONCLUIDO,
        PENDENTE_MATRICULA
    }

    public enum FormaIngresso {
        MATRICULA_NOVA,
        TRANSFERENCIA_INTERNA,
        TRANSFERENCIA_EXTERNA,
        REINGRESSANTE
    }
}
