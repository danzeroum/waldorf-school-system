package br.edu.waldorf.modules.pessoa.domain.model;

import br.edu.waldorf.core.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pessoas", indexes = {
    @Index(name = "idx_tipo_ativo", columnList = "tipo, ativo"),
    @Index(name = "idx_cpf",        columnList = "cpf"),
    @Index(name = "idx_email",      columnList = "email"),
    @Index(name = "idx_lgpd",       columnList = "lgpd_consentimento_geral, data_exclusao_prevista")
})
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Pessoa extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoPessoa tipo;

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 200)
    @Column(name = "nome_completo", nullable = false, length = 200)
    private String nomeCompleto;

    @Size(max = 14)
    @Column(name = "cpf", unique = true, length = 14)
    private String cpf;

    @Size(max = 20)
    @Column(name = "rg", length = 20)
    private String rg;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "telefone_principal", length = 20)
    private String telefonePrincipal;

    @Column(name = "telefone_secundario", length = 20)
    private String telefoneSecundario;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private Boolean ativo = true;

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

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Endereco> enderecos = new ArrayList<>();

    public void adicionarEndereco(Endereco endereco) { enderecos.add(endereco); endereco.setPessoa(this); }
    public void removerEndereco(Endereco endereco)   { enderecos.remove(endereco); endereco.setPessoa(null); }
    public void registrarConsentimentoLGPD()  { this.lgpdConsentimentoGeral = true; this.lgpdDataConsentimento = LocalDateTime.now(); }
    public void revogarConsentimentoLGPD()    { this.lgpdConsentimentoGeral = false; }
    public void inativar()  { this.ativo = false; }
    public void reativar()  { this.ativo = true; }

    public enum TipoPessoa       { ALUNO, RESPONSAVEL, PROFESSOR, FUNCIONARIO, OUTRO }
    public enum BaseLegalLGPD    { CONSENTIMENTO, CONTRATO, LEGITIMO_INTERESSE, OBRIGACAO_LEGAL }
    public enum ClassificacaoDados { PUBLICO, INTERNO, CONFIDENCIAL, SENSIVEL }
}
