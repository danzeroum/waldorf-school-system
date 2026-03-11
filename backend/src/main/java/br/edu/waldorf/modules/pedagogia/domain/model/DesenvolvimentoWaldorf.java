package br.edu.waldorf.modules.pedagogia.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.pessoa.domain.model.Aluno;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidade DesenvolvimentoWaldorf - ficha de desenvolvimento individual do aluno
 * Mapeia a tabela 'desenvolvimento_waldorf'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(
    name = "desenvolvimento_waldorf",
    indexes = {
        @Index(name = "idx_aluno", columnList = "aluno_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesenvolvimentoWaldorf extends BaseEntity {

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false, unique = true)
    private Aluno aluno;

    @Column(name = "ritmo_sono", columnDefinition = "TEXT")
    private String ritmoSono;

    @Column(name = "alimentacao_observacoes", columnDefinition = "TEXT")
    private String alimentacaoObservacoes;

    @Column(name = "desenvolvimento_motor", columnDefinition = "TEXT")
    private String desenvolvimentoMotor;

    @Column(name = "desenvolvimento_social", columnDefinition = "TEXT")
    private String desenvolvimentoSocial;

    @Column(name = "despertar_cognitivo", columnDefinition = "TEXT")
    private String despertarCognitivo;

    @Column(name = "despertar_animico", columnDefinition = "TEXT")
    private String despertarAnimico;

    @Column(name = "saude_observacoes", columnDefinition = "TEXT")
    private String saudeObservacoes;

    @Column(name = "anotacoes_importantes", columnDefinition = "TEXT")
    private String anotacoesImportantes;

    @Column(name = "preferencias_brincadeiras", columnDefinition = "TEXT")
    private String preferenciasBrincadeiras;

    @Column(name = "relacionamento_natureza", columnDefinition = "TEXT")
    private String relacionamentoNatureza;

    @Column(name = "data_ultima_avaliacao")
    private LocalDate dataUltimaAvaliacao;
}
