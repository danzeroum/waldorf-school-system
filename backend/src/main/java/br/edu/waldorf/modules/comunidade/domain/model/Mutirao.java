package br.edu.waldorf.modules.comunidade.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Mutirao - mutirao comunitario Waldorf
 * Mapeia a tabela 'mutiroes'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Entity
@Table(
    name = "mutiroes",
    indexes = @Index(name = "idx_mutirao_data_status", columnList = "data_mutirao, status")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Mutirao extends BaseEntity {

    @NotBlank
    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @NotNull
    @Column(name = "data_mutirao", nullable = false)
    private LocalDate dataMutirao;

    @Column(name = "horario_inicio")
    private LocalTime horarioInicio;

    @Column(name = "horario_fim")
    private LocalTime horarioFim;

    @Column(name = "local_mutirao", length = 200)
    private String localMutirao;

    @Column(name = "materiais_necessarios", columnDefinition = "TEXT")
    private String materiaisNecessarios;

    @Column(name = "limite_participantes")
    private Integer limiteParticipantes;

    @Column(name = "permite_criancas")
    @Builder.Default
    private Boolean permiteCriancas = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15)
    @Builder.Default
    private StatusMutirao status = StatusMutirao.PLANEJADO;

    @OneToMany(mappedBy = "eventoId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InscricaoEvento> inscricoes = new ArrayList<>();

    // --- Metodos de negocio ---

    public void confirmar() {
        if (this.status != StatusMutirao.PLANEJADO)
            throw new IllegalStateException("Apenas PLANEJADOS podem ser confirmados");
        this.status = StatusMutirao.CONFIRMADO;
    }

    public void concluir() { this.status = StatusMutirao.CONCLUIDO; }
    public void cancelar() { this.status = StatusMutirao.CANCELADO; }

    public boolean possuiVagas() {
        if (limiteParticipantes == null) return true;
        return inscricoes.stream().mapToInt(InscricaoEvento::getNumeroPessoas).sum() < limiteParticipantes;
    }

    public enum StatusMutirao { PLANEJADO, CONFIRMADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO }
}
