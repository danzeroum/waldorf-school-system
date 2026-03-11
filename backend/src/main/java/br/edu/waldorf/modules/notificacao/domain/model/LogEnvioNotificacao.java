package br.edu.waldorf.modules.notificacao.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.security.domain.model.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade LogEnvioNotificacao - rastreio de todas as notificacoes enviadas
 * Mapeia a tabela 'logs_envio_notificacoes'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Entity
@Table(
    name = "logs_envio_notificacoes",
    indexes = {
        @Index(name = "idx_log_usuario_status",  columnList = "usuario_id, status_envio"),
        @Index(name = "idx_log_tipo_data",        columnList = "tipo_conteudo, data_hora_envio_planejado"),
        @Index(name = "idx_log_referencia",       columnList = "referencia_tipo, referencia_id")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LogEnvioNotificacao extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conteudo", nullable = false, length = 40)
    private TipoConteudo tipoConteudo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false, length = 10)
    private PreferenciaNotificacao.CanalEnvio canal;

    @NotBlank
    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "conteudo", columnDefinition = "TEXT")
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_envio", length = 15)
    @Builder.Default
    private StatusEnvio statusEnvio = StatusEnvio.PENDENTE;

    @Column(name = "motivo_supressao", length = 200)
    private String motivoSupressao;

    @NotNull
    @Column(name = "data_hora_envio_planejado", nullable = false)
    private LocalDateTime dataHoraEnvioPlanejado;

    @Column(name = "data_hora_envio_real")
    private LocalDateTime dataHoraEnvioReal;

    @Column(name = "data_hora_leitura")
    private LocalDateTime dataHoraLeitura;

    @Column(name = "tentativas")
    @Builder.Default
    private Integer tentativas = 0;

    @Column(name = "erro_detalhes", columnDefinition = "TEXT")
    private String erroDetalhes;

    @Column(name = "referencia_tipo", length = 50)
    private String referenciaTipo;

    @Column(name = "referencia_id")
    private Long referenciaId;

    // --- Metodos de negocio ---

    public void marcarEnviado() {
        this.statusEnvio = StatusEnvio.ENVIADO;
        this.dataHoraEnvioReal = LocalDateTime.now();
    }

    public void marcarEntregue() {
        this.statusEnvio = StatusEnvio.ENTREGUE;
    }

    public void marcarLido() {
        this.statusEnvio = StatusEnvio.LIDO;
        this.dataHoraLeitura = LocalDateTime.now();
    }

    public void marcarFalha(String detalhes) {
        this.statusEnvio = StatusEnvio.FALHA;
        this.erroDetalhes = detalhes;
        this.tentativas++;
    }

    public void suprimir(String motivo) {
        this.statusEnvio = StatusEnvio.SUPRIMIDO;
        this.motivoSupressao = motivo;
    }

    public enum TipoConteudo {
        OBSERVACAO_NOVA, RELATORIO_PRONTO, MENSALIDADE_GERADA, MENSALIDADE_ATRASADA,
        EVENTO_PROXIMO, COMUNICADO_GERAL, EMERGENCIA_LOGISTICA
    }

    public enum StatusEnvio { PENDENTE, ENVIADO, ENTREGUE, LIDO, FALHA, SUPRIMIDO }
}
