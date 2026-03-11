package br.edu.waldorf.modules.pedagogia.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.escolar.domain.model.Turma;
import br.edu.waldorf.modules.pessoa.domain.model.Aluno;
import br.edu.waldorf.modules.pessoa.domain.model.Professor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade RelatórioNarrativo - relatório Waldorf por ciclo
 * Mapeia a tabela 'relatorios_narrativos'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Entity
@Table(
    name = "relatorios_narrativos",
    indexes = {
        @Index(name = "idx_aluno_ciclo",  columnList = "aluno_id, ciclo"),
        @Index(name = "idx_status_data",  columnList = "status, data_elaboracao")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatorioNarrativo extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @NotBlank
    @Column(name = "ciclo", nullable = false, length = 50)
    private String ciclo;

    @NotBlank
    @Column(name = "periodo", nullable = false, length = 50)
    private String periodo;

    @NotBlank
    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    // Estrutura narrativa Waldorf
    @Column(name = "texto_desenvolvimento_fisico",    columnDefinition = "TEXT")
    private String textoDesenvolvimentoFisico;

    @Column(name = "texto_desenvolvimento_animico",   columnDefinition = "TEXT")
    private String textoDesenvolvimentoAnimico;

    @Column(name = "texto_desenvolvimento_cognitivo", columnDefinition = "TEXT")
    private String textoDesenvolvimentoCognitivo;

    @Column(name = "texto_relacao_social",            columnDefinition = "TEXT")
    private String textoRelacaoSocial;

    @Column(name = "texto_observacoes_artisticas",    columnDefinition = "TEXT")
    private String textoObservacoesArtisticas;

    @Column(name = "texto_trabalhos_manuais",         columnDefinition = "TEXT")
    private String textoTrabalhosManuais;

    @Column(name = "texto_conclusao_convite",         columnDefinition = "TEXT")
    private String textoConclusaoConvite;

    // Controle
    @NotNull
    @Column(name = "data_elaboracao", nullable = false)
    private LocalDate dataElaboracao;

    @Column(name = "data_entrega_pais")
    private LocalDate dataEntregaPais;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private StatusRelatorio status = StatusRelatorio.RASCUNHO;

    @Column(name = "arquivo_pdf_assinado", length = 500)
    private String arquivoPdfAssinado;

    @Column(name = "confirmacao_leitura_responsavel")
    @Builder.Default
    private Boolean confirmacaoLeituraResponsavel = false;

    @Column(name = "data_confirmacao_leitura")
    private LocalDateTime dataConfirmacaoLeitura;

    // --- Métodos de negócio ---

    public void enviarParaRevisao() {
        if (this.status != StatusRelatorio.RASCUNHO) {
            throw new IllegalStateException("Somente RASCUNHO pode ser enviado para revisão");
        }
        this.status = StatusRelatorio.REVISAO;
    }

    public void aprovar() {
        this.status = StatusRelatorio.APROVADO;
    }

    public void entregar() {
        if (this.status != StatusRelatorio.APROVADO) {
            throw new IllegalStateException("Somente relatórios APROVADOS podem ser entregues");
        }
        this.status = StatusRelatorio.ENTREGUE;
        this.dataEntregaPais = LocalDate.now();
    }

    public void confirmarLeitura() {
        this.confirmacaoLeituraResponsavel = true;
        this.dataConfirmacaoLeitura = LocalDateTime.now();
    }

    // --- Enums ---

    public enum StatusRelatorio {
        RASCUNHO,
        REVISAO,
        APROVADO,
        ENTREGUE
    }
}
