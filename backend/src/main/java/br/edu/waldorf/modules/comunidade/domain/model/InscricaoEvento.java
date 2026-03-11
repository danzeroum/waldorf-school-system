package br.edu.waldorf.modules.comunidade.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.pessoa.domain.model.Pessoa;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entidade InscricaoEvento - inscricao em festival ou mutirao
 * Mapeia a tabela 'inscricoes_eventos'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Entity
@Table(
    name = "inscricoes_eventos",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_evento_pessoa",
        columnNames = {"tipo_evento", "evento_id", "pessoa_id"}
    ),
    indexes = @Index(name = "idx_inscricao_evento", columnList = "tipo_evento, evento_id")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InscricaoEvento extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 10)
    private TipoEvento tipoEvento;

    @NotNull
    @Column(name = "evento_id", nullable = false)
    private Long eventoId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;

    @Column(name = "numero_pessoas")
    @Builder.Default
    private Integer numeroPessoas = 1;

    @Column(name = "criancas_incluidas")
    @Builder.Default
    private Integer criancasIncluidas = 0;

    @Column(name = "materiais_trazidos", columnDefinition = "TEXT")
    private String materiaisTrazidos;

    @Column(name = "confirmado")
    @Builder.Default
    private Boolean confirmado = false;

    public enum TipoEvento { FESTIVAL, MUTIRAO }
}
