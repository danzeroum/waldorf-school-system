package br.edu.waldorf.modules.pessoa.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Pessoa - Super tipo para todos os tipos de pessoas do sistema
 * (Aluno, Professor, Responsável, Funcionário)
 * 
 * Mapeia a tabela 'pessoas' com suporte a herança JOINED
 * 
 * @author Daniel Lau
 * @version 1.0.0
 * @since 2026-01-31
 */
@Entity
@Table(name = "pessoas", indexes = {
    @Index(name = "idx_tipo_ativo", columnList = "tipo, ativo"),
    @Index(name = "idx_cpf", columnList = "cpf"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_lgpd", columnList = "lgpd_consentimento_geral, data_exclusao_prevista")
})
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pessoa extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoPessoa tipo;

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 200, message = "Nome deve ter entre 3 e 200 caracteres")
    @Column(name = "nome_completo", nullable = false, length = 200)
    private String nomeCompleto;

    @Size(max = 14, message = "CPF deve ter no máximo 14 caracteres")
    @Column(name = "cpf", unique = true, length = 14)
    private String cpf;

    @Size(max = 20, message = "RG deve ter no máximo 20 caracteres")
    @Column(name = "rg", length = 20)
    private String rg;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Column(name = "telefone_principal", length = 20)
    private String telefonePrincipal;

    @Size(max = 20, message = "Telefone secundário deve ter no máximo 20 caracteres")
    @Column(name = "telefone_secundario", length = 20)
    private String telefoneSecundario;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    // --- Campos LGPD ---
    @Column(name = "lgpd_consentimento_geral")
    @Builder.Default
    private Boolean lgpdConsentimentoGeral = false;

    @Column(name = "lgpd_data_consentimento")
    private LocalDateTime lgpdDataConsentimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "lgpd_base_legal", length = 30)
    @Builder.Default
    private BaseLegalLGPD lgpdBaseLegal = BaseLegalLGPD.CONSENTIMENTO;

    @Enumerated(EnumType.STRING)
    @Column(name = "classificacao_dados", length = 20)
    @Builder.Default
    private ClassificacaoDados classificacaoDados = ClassificacaoDados.INTERNO;

    @Column(name = "data_exclusao_prevista")
    private LocalDate dataExclusaoPrevista;

    // --- Relacionamentos ---
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Endereco> enderecos = new ArrayList<>();

    // --- Métodos de Negócio ---
    
    /**
     * Adiciona um endereço à pessoa
     * 
     * @param endereco Endereço a ser adicionado
     */
    public void adicionarEndereco(Endereco endereco) {
        enderecos.add(endereco);
        endereco.setPessoa(this);
    }

    /**
     * Remove um endereço da pessoa
     * 
     * @param endereco Endereço a ser removido
     */
    public void removerEndereco(Endereco endereco) {
        enderecos.remove(endereco);
        endereco.setPessoa(null);
    }

    /**
     * Registra consentimento LGPD
     */
    public void registrarConsentimentoLGPD() {
        this.lgpdConsentimentoGeral = true;
        this.lgpdDataConsentimento = LocalDateTime.now();
    }

    /**
     * Revoga consentimento LGPD
     */
    public void revogarConsentimentoLGPD() {
        this.lgpdConsentimentoGeral = false;
    }

    /**
     * Inativa a pessoa
     */
    public void inativar() {
        this.ativo = false;
    }

    /**
     * Reativa a pessoa
     */
    public void reativar() {
        this.ativo = true;
    }

    // --- Enums ---

    public enum TipoPessoa {
        ALUNO,
        RESPONSAVEL,
        PROFESSOR,
        FUNCIONARIO,
        OUTRO
    }

    public enum BaseLegalLGPD {
        CONSENTIMENTO,
        CONTRATO,
        LEGITIMO_INTERESSE,
        OBRIGACAO_LEGAL
    }

    public enum ClassificacaoDados {
        PUBLICO,
        INTERNO,
        CONFIDENCIAL,
        SENSIVEL
    }
}
