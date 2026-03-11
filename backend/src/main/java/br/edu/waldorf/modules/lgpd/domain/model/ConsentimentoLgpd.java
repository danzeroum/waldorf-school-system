package br.edu.waldorf.modules.lgpd.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.pessoa.domain.model.Pessoa;
import br.edu.waldorf.modules.security.domain.model.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade ConsentimentoLgpd - registro de consentimento LGPD por finalidade
 * Mapeia a tabela 'consentimentos_lgpd'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Entity
@Table(
    name = "consentimentos_lgpd",
    indexes = {
        @Index(name = "idx_consentimento_pessoa",  columnList = "pessoa_id, finalidade"),
        @Index(name = "idx_consentimento_data",     columnList = "consentido, data_consentimento")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConsentimentoLgpd extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;

    @NotBlank
    @Column(name = "finalidade", nullable = false, length = 200)
    private String finalidade;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @NotNull
    @Column(name = "consentido", nullable = false)
    private Boolean consentido;

    @NotNull
    @Column(name = "data_consentimento", nullable = false)
    private LocalDateTime dataConsentimento;

    @Column(name = "data_revogacao")
    private LocalDateTime dataRevogacao;

    @Column(name = "ip_consentimento", length = 45)
    private String ipConsentimento;

    @Column(name = "versao_termos", length = 20)
    private String versaoTermos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coletado_por")
    private Usuario coletadoPor;

    // --- Metodos de negocio ---

    public void revogar() {
        if (!Boolean.TRUE.equals(this.consentido)) {
            throw new IllegalStateException("Consentimento ja foi negado ou revogado");
        }
        this.consentido = false;
        this.dataRevogacao = LocalDateTime.now();
    }

    public boolean isAtivo() {
        return Boolean.TRUE.equals(consentido) && dataRevogacao == null;
    }
}
