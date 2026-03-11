package br.edu.waldorf.modules.comunidade.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.escolar.domain.model.Turma;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade CanalComunicacao - canais de mensagens por tipo
 * Mapeia a tabela 'canais_comunicacao'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Entity
@Table(
    name = "canais_comunicacao",
    indexes = @Index(name = "idx_canal_tipo_ativo", columnList = "tipo, ativo")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CanalComunicacao extends BaseEntity {

    @NotBlank
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoCanal tipo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "regras_engajamento", columnDefinition = "TEXT")
    private String regrasEngajamento;

    @Column(name = "publico")
    @Builder.Default
    private Boolean publico = true;

    @Column(name = "moderado")
    @Builder.Default
    private Boolean moderado = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id")
    private Turma turma;

    @Column(name = "ativo")
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "cor_canal", length = 7)
    @Builder.Default
    private String corCanal = "#9C27B0";

    @OneToMany(mappedBy = "canal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MensagemCanal> mensagens = new ArrayList<>();

    public enum TipoCanal {
        TURMA, COMISSAO, FESTIVAL, GERAL, DIRETORIA, PAIS, PROFESSORES
    }
}
