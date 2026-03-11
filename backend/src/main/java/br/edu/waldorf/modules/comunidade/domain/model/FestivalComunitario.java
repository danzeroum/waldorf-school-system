package br.edu.waldorf.modules.comunidade.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.pessoa.domain.model.Pessoa;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade FestivalComunitario - eventos sazonais Waldorf
 * Mapeia a tabela 'festivais_comunitarios'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Entity
@Table(
    name = "festivais_comunitarios",
    indexes = @Index(name = "idx_festival_data_status", columnList = "data_evento, status")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FestivalComunitario extends BaseEntity {

    @NotBlank
    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 25)
    private TipoFestival tipo;

    @NotNull
    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @Column(name = "horario_inicio")
    private LocalTime horarioInicio;

    @Column(name = "horario_fim")
    private LocalTime horarioFim;

    @Column(name = "local_evento", length = 200)
    private String localEvento;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private Pessoa responsavel;

    @Column(name = "limite_participantes")
    private Integer limiteParticipantes;

    @Column(name = "aberto_comunidade")
    @Builder.Default
    private Boolean abertoComunidade = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15)
    @Builder.Default
    private StatusEvento status = StatusEvento.PLANEJADO;

    @OneToMany(mappedBy = "eventoId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InscricaoEvento> inscricoes = new ArrayList<>();

    // --- Metodos de negocio ---

    public void confirmar() {
        if (this.status != StatusEvento.PLANEJADO)
            throw new IllegalStateException("Apenas eventos PLANEJADOS podem ser confirmados");
        this.status = StatusEvento.CONFIRMADO;
    }

    public void iniciar() {
        if (this.status != StatusEvento.CONFIRMADO)
            throw new IllegalStateException("Apenas eventos CONFIRMADOS podem ser iniciados");
        this.status = StatusEvento.EM_ANDAMENTO;
    }

    public void concluir() { this.status = StatusEvento.CONCLUIDO; }
    public void cancelar() { this.status = StatusEvento.CANCELADO; }

    public boolean possuiVagas() {
        if (limiteParticipantes == null) return true;
        return inscricoes.stream().mapToInt(InscricaoEvento::getNumeroPessoas).sum() < limiteParticipantes;
    }

    public enum TipoFestival   { FESTIVAL_SAZONAL, BAZAR, APRESENTACAO, REUNIAO_PAIS, FEIRA, OUTRO }
    public enum StatusEvento   { PLANEJADO, CONFIRMADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO }
}
