package br.edu.waldorf.modules.escolar.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entidade Curso - Etapas Waldorf (Jardim, Infantil, Fundamental, Médio)
 * Mapeia a tabela 'cursos'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(name = "cursos", indexes = {
    @Index(name = "idx_nivel", columnList = "nivel_ensino")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curso extends BaseEntity {

    @NotBlank
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_ensino", length = 20)
    private NivelEnsino nivelEnsino;

    @Column(name = "serie_inicial")
    private Integer serieInicial;

    @Column(name = "serie_final")
    private Integer serieFinal;

    @Column(name = "idade_recomendada_inicial")
    private Integer idadeRecomendadaInicial;

    @Column(name = "idade_recomendada_final")
    private Integer idadeRecomendadaFinal;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "objetivos_pedagogicos", columnDefinition = "TEXT")
    private String objetivosPedagogicos;

    @Column(name = "ativo")
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "ordem_exibicao")
    private Integer ordemExibicao;

    @Column(name = "cor_identificacao", length = 7)
    @Builder.Default
    private String corIdentificacao = "#4CAF50";

    public enum NivelEnsino {
        JARDIM,
        INFANTIL,
        FUNDAMENTAL_I,
        FUNDAMENTAL_II,
        ENSINO_MEDIO,
        EJA
    }
}
