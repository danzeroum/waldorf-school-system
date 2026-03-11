package br.edu.waldorf.modules.pedagogia.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.pessoa.domain.model.Aluno;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidade PortfolioItem - portfólio digital do aluno
 * Mapeia a tabela 'portfolio_itens'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(
    name = "portfolio_itens",
    indexes = {
        @Index(name = "idx_aluno_tipo",    columnList = "aluno_id, tipo"),
        @Index(name = "idx_data_criacao",  columnList = "data_criacao")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioItem extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 20)
    private TipoPortfolio tipo;

    @NotBlank
    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "tecnica", length = 100)
    private String tecnica;

    @Column(name = "materiais", length = 200)
    private String materiais;

    @Column(name = "data_criacao")
    private LocalDate dataCriacao;

    @Column(name = "arquivo_url", length = 500)
    private String arquivoUrl;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "visivel_pais")
    @Builder.Default
    private Boolean visivelPais = true;

    @Column(name = "visivel_galeria_publica")
    @Builder.Default
    private Boolean visivelGaleriaPublica = false;

    @Column(name = "permitir_comentarios")
    @Builder.Default
    private Boolean permitirComentarios = true;

    // --- Enums ---

    public enum TipoPortfolio {
        MANUAL_WORK,
        ARTWORK,
        MUSIC,
        THEATER,
        WRITING,
        OTHER
    }
}
