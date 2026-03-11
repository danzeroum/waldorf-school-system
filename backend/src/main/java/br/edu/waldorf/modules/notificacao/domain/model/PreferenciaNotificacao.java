package br.edu.waldorf.modules.notificacao.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.security.domain.model.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalTime;

/**
 * Entidade PreferenciaNotificacao - configuracao de notificacoes por usuario/categoria
 * Mapeia a tabela 'preferencias_notificacao'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Entity
@Table(
    name = "preferencias_notificacao",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_usuario_categoria",
        columnNames = {"usuario_id", "categoria"}
    ),
    indexes = @Index(name = "idx_preferencia_usuario", columnList = "usuario_id")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PreferenciaNotificacao extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 20)
    private CategoriaNotificacao categoria;

    @Column(name = "canal_email")
    @Builder.Default
    private Boolean canalEmail = true;

    @Column(name = "canal_push")
    @Builder.Default
    private Boolean canalPush = true;

    @Column(name = "canal_sms")
    @Builder.Default
    private Boolean canalSms = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "agregacao", length = 20)
    @Builder.Default
    private Agregacao agregacao = Agregacao.IMEDIATO;

    @Column(name = "horario_resumo")
    @Builder.Default
    private LocalTime horarioResumo = LocalTime.of(18, 0);

    @Column(name = "silencio_inicio")
    @Builder.Default
    private LocalTime silencioInicio = LocalTime.of(20, 0);

    @Column(name = "silencio_fim")
    @Builder.Default
    private LocalTime silencioFim = LocalTime.of(7, 0);

    @Column(name = "ativo")
    @Builder.Default
    private Boolean ativo = true;

    // --- Logica de silencio ---

    /**
     * Verifica se o horario atual esta dentro da janela de silencio.
     * Suporta janelas que cruzam meia-noite (ex: 20h -> 07h).
     */
    public boolean estaEmSilencio(LocalTime agora) {
        if (silencioInicio == null || silencioFim == null) return false;
        if (silencioInicio.isBefore(silencioFim)) {
            return !agora.isBefore(silencioInicio) && agora.isBefore(silencioFim);
        } else {
            // Janela cruza meia-noite
            return !agora.isBefore(silencioInicio) || agora.isBefore(silencioFim);
        }
    }

    public boolean aceitaCanal(CanalEnvio canal) {
        return switch (canal) {
            case EMAIL  -> Boolean.TRUE.equals(canalEmail);
            case PUSH   -> Boolean.TRUE.equals(canalPush);
            case SMS    -> Boolean.TRUE.equals(canalSms);
            case IN_APP -> true; // IN_APP sempre ativo
        };
    }

    public enum CategoriaNotificacao {
        PEDAGOGICO, ADMINISTRATIVO, FINANCEIRO, COMUNIDADE, EMERGENCIA, SISTEMA
    }

    public enum Agregacao { IMEDIATO, RESUMO_DIARIO, RESUMO_SEMANAL }

    public enum CanalEnvio { EMAIL, PUSH, SMS, IN_APP }
}
