package br.edu.waldorf.modules.pessoa.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade de junção Responsável-Aluno
 * Mapeia a tabela 'responsaveis_alunos'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(
    name = "responsaveis_alunos",
    uniqueConstraints = @UniqueConstraint(name = "uk_responsavel_aluno", columnNames = {"responsavel_id", "aluno_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponsavelAluno extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsavel_id", nullable = false)
    private Responsavel responsavel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_relacao", length = 30)
    private Responsavel.TipoRelacao tipoRelacao;

    @Column(name = "autorizado_buscar")
    @Builder.Default
    private Boolean autorizadoBuscar = true;

    @Column(name = "contato_emergencia")
    @Builder.Default
    private Boolean contatoEmergencia = false;

    @Column(name = "guarda_compartilhada")
    @Builder.Default
    private Boolean guardaCompartilhada = false;

    @Column(name = "prioridade_contato")
    @Builder.Default
    private Integer prioridadeContato = 1;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;
}
