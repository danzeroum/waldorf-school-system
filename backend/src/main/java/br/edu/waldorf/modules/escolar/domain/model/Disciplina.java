package br.edu.waldorf.modules.escolar.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entidade Disciplina - Áreas do conhecimento Waldorf
 * Mapeia a tabela 'disciplinas'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(name = "disciplinas", indexes = {
    @Index(name = "idx_area", columnList = "area_conhecimento")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Disciplina extends BaseEntity {

    @NotBlank
    @Column(name = "codigo", unique = true, nullable = false, length = 20)
    private String codigo;

    @NotBlank
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "carga_horaria_total")
    private Integer cargaHorariaTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "area_conhecimento", length = 30)
    private AreaConhecimento areaConhecimento;

    @Column(name = "ativo")
    @Builder.Default
    private Boolean ativo = true;

    public enum AreaConhecimento {
        LINGUAGENS,
        MATEMATICA,
        CIENCIAS_NATUREZA,
        CIENCIAS_HUMANAS,
        ARTES,
        TRABALHOS_MANUAIS
    }
}
