package br.edu.waldorf.modules.comunidade.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import br.edu.waldorf.modules.security.domain.model.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade MensagemCanal - mensagem em um canal de comunicacao
 * Mapeia a tabela 'mensagens_canal'
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Entity
@Table(
    name = "mensagens_canal",
    indexes = @Index(name = "idx_mensagem_canal_data", columnList = "canal_id, created_at")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MensagemCanal extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "canal_id", nullable = false)
    private CanalComunicacao canal;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @NotBlank
    @Column(name = "conteudo", nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 15)
    @Builder.Default
    private TipoMensagem tipo = TipoMensagem.TEXTO;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridade", length = 10)
    @Builder.Default
    private Prioridade prioridade = Prioridade.NORMAL;

    @Column(name = "fixada")
    @Builder.Default
    private Boolean fixada = false;

    @Column(name = "editada")
    @Builder.Default
    private Boolean editada = false;

    @Column(name = "data_edicao")
    private LocalDateTime dataEdicao;

    // --- Metodos de negocio ---

    public void editar(String novoConteudo) {
        this.conteudo = novoConteudo;
        this.editada = true;
        this.dataEdicao = LocalDateTime.now();
    }

    public void fixar()   { this.fixada = true;  }
    public void desafixar() { this.fixada = false; }

    public enum TipoMensagem  { TEXTO, AVISO, ANUNCIO, ENQUETE }
    public enum Prioridade    { BAIXA, NORMAL, ALTA, URGENTE }
}
