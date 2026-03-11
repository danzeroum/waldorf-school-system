package br.edu.waldorf.modules.pessoa.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Responsável - especialização de Pessoa
 * Mapeia a tabela 'responsaveis' com herança JOINED de 'pessoas'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(name = "responsaveis", indexes = {
    @Index(name = "idx_responsavel_situacao", columnList = "situacao")
})
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Responsavel extends Pessoa {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_relacao", length = 30)
    private TipoRelacao tipoRelacao;

    @Column(name = "profissao", length = 100)
    private String profissao;

    @Column(name = "local_trabalho", length = 200)
    private String localTrabalho;

    @Column(name = "telefone_trabalho", length = 20)
    private String telefoneTrabalho;

    @Column(name = "autorizado_buscar")
    @Builder.Default
    private Boolean autorizadoBuscar = true;

    @Column(name = "contato_emergencia")
    @Builder.Default
    private Boolean contatoEmergencia = false;

    @Column(name = "guarda_compartilhada")
    @Builder.Default
    private Boolean guardaCompartilhada = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", length = 20)
    @Builder.Default
    private SituacaoResponsavel situacao = SituacaoResponsavel.ATIVO;

    @Column(name = "prioridade_contato")
    @Builder.Default
    private Integer prioridadeContato = 1;

    // --- Relacionamentos ---
    @OneToMany(mappedBy = "responsavel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ResponsavelAluno> alunos = new ArrayList<>();

    // --- Métodos de negócio ---

    public boolean isPrincipal() {
        return this.prioridadeContato != null && this.prioridadeContato == 1;
    }

    // --- Enums ---

    public enum TipoRelacao {
        PAI,
        MAE,
        AVO,
        AVO_MATERNO,
        AVO_PATERNO,
        TIO,
        RESPONSAVEL_LEGAL,
        OUTRO
    }

    public enum SituacaoResponsavel {
        ATIVO,
        INATIVO
    }
}
