package br.edu.waldorf.modules.lgpd.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.pessoa.domain.model.Pessoa;
import br.edu.waldorf.modules.security.domain.model.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade SolicitacaoTitular - solicitacao LGPD de titular de dados
 * Mapeia a tabela 'solicitacoes_titulares'
 * Prazo legal de resposta: 15 dias uteis
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Entity
@Table(
    name = "solicitacoes_titulares",
    indexes = {
        @Index(name = "idx_solicitacao_pessoa_tipo",  columnList = "pessoa_id, tipo_solicitacao"),
        @Index(name = "idx_solicitacao_status_prazo", columnList = "status, prazo_resposta")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SolicitacaoTitular extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_solicitacao", nullable = false, length = 20)
    private TipoSolicitacao tipoSolicitacao;

    @NotBlank
    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private StatusSolicitacao status = StatusSolicitacao.ABERTA;

    @Column(name = "data_solicitacao")
    @Builder.Default
    private LocalDateTime dataSolicitacao = LocalDateTime.now();

    @NotNull
    @Column(name = "prazo_resposta", nullable = false)
    private LocalDate prazoResposta;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "resposta", columnDefinition = "TEXT")
    private String resposta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendido_por")
    private Usuario atendidoPor;

    @Column(name = "justificativa_rejeicao", columnDefinition = "TEXT")
    private String justificativaRejeicao;

    // --- Metodos de negocio ---

    public void iniciarAnalise() {
        if (this.status != StatusSolicitacao.ABERTA)
            throw new IllegalStateException("Solicitacao nao esta ABERTA");
        this.status = StatusSolicitacao.EM_ANALISE;
    }

    public void iniciarAtendimento() {
        if (this.status != StatusSolicitacao.EM_ANALISE)
            throw new IllegalStateException("Solicitacao nao esta EM_ANALISE");
        this.status = StatusSolicitacao.EM_ATENDIMENTO;
    }

    public void concluir(String resposta, Usuario atendente) {
        this.status = StatusSolicitacao.CONCLUIDA;
        this.resposta = resposta;
        this.atendidoPor = atendente;
        this.dataConclusao = LocalDateTime.now();
    }

    public void rejeitar(String justificativa, Usuario atendente) {
        this.status = StatusSolicitacao.REJEITADA;
        this.justificativaRejeicao = justificativa;
        this.atendidoPor = atendente;
        this.dataConclusao = LocalDateTime.now();
    }

    public boolean estaNoPrazo() {
        return LocalDate.now().isBefore(prazoResposta) || LocalDate.now().isEqual(prazoResposta);
    }

    public boolean estaAtrasada() {
        return !estaNoPrazo() &&
               (status == StatusSolicitacao.ABERTA ||
                status == StatusSolicitacao.EM_ANALISE ||
                status == StatusSolicitacao.EM_ATENDIMENTO);
    }

    public enum TipoSolicitacao  { ACESSO, CORRECAO, EXCLUSAO, PORTABILIDADE, REVOGACAO, INFORMACAO }
    public enum StatusSolicitacao { ABERTA, EM_ANALISE, EM_ATENDIMENTO, CONCLUIDA, REJEITADA }
}
