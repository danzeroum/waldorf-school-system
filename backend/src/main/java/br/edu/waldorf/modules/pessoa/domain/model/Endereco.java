package br.edu.waldorf.modules.pessoa.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entidade Endereco - Representa os endereços das pessoas
 * Mapeia a tabela 'enderecos'
 * 
 * @author Daniel Lau
 * @version 1.0.0
 * @since 2026-01-31
 */
@Entity
@Table(name = "enderecos", indexes = {
    @Index(name = "idx_pessoa", columnList = "pessoa_id"),
    @Index(name = "idx_cidade_estado", columnList = "cidade, estado")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endereco extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;

    @Size(max = 10, message = "CEP deve ter no máximo 10 caracteres")
    @Column(name = "cep", length = 10)
    private String cep;

    @Size(max = 200, message = "Logradouro deve ter no máximo 200 caracteres")
    @Column(name = "logradouro", length = 200)
    private String logradouro;

    @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
    @Column(name = "numero", length = 10)
    private String numero;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    @Column(name = "complemento", length = 100)
    private String complemento;

    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    @Column(name = "bairro", length = 100)
    private String bairro;

    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    @Column(name = "cidade", length = 100)
    private String cidade;

    @Size(max = 2, message = "Estado deve ter 2 caracteres")
    @Column(name = "estado", columnDefinition = "CHAR(2)", length = 2)
    private String estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 20)
    @Builder.Default
    private TipoEndereco tipo = TipoEndereco.RESIDENCIAL;

    @Column(name = "principal")
    @Builder.Default
    private Boolean principal = true;

    // --- Métodos de Negócio ---

    /**
     * Formata o endereço completo em uma string
     * 
     * @return Endereço formatado
     */
    @Transient
    public String getEnderecoCompleto() {
        StringBuilder sb = new StringBuilder();
        
        if (logradouro != null) {
            sb.append(logradouro);
        }
        
        if (numero != null) {
            sb.append(", ").append(numero);
        }
        
        if (complemento != null && !complemento.isBlank()) {
            sb.append(" - ").append(complemento);
        }
        
        if (bairro != null) {
            sb.append(", ").append(bairro);
        }
        
        if (cidade != null) {
            sb.append(", ").append(cidade);
        }
        
        if (estado != null) {
            sb.append(" - ").append(estado);
        }
        
        if (cep != null) {
            sb.append(", CEP: ").append(cep);
        }
        
        return sb.toString();
    }

    /**
     * Define este endereço como principal
     */
    public void tornarPrincipal() {
        this.principal = true;
    }

    /**
     * Define este endereço como secundário
     */
    public void tornarSecundario() {
        this.principal = false;
    }

    // --- Enums ---

    public enum TipoEndereco {
        RESIDENCIAL,
        COMERCIAL
    }

    @Override
    public String toString() {
        return "Endereco{" +
                "id=" + getId() +
                ", tipo=" + tipo +
                ", principal=" + principal +
                ", enderecoCompleto='" + getEnderecoCompleto() + '\'' +
                '}';
    }
}
