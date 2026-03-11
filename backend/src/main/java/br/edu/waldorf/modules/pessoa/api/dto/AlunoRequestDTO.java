package br.edu.waldorf.modules.pessoa.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO para criação/atualização de Aluno
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlunoRequestDTO {

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 200)
    private String nomeCompleto;

    private String nomeSocial;

    @Size(max = 14)
    private String cpf;

    private LocalDate dataNascimento;

    @NotBlank(message = "Email é obrigatório")
    @Email
    private String email;

    @Size(max = 20)
    private String telefonePrincipal;

    private String fotoUrl;

    // Dados médicos
    private String tipoSanguineo;
    private String planoSaude;
    private String alergias;
    private String medicamentosControlados;
    private String necessidadesEspeciais;
    private String observacoesMedicas;

    // Dados Waldorf
    private String temperamento;
    private String naturalidade;
    private String nacionalidade;
    private String nomePai;
    private String nomeMae;
    private Integer anoIngresso;
    private String formaIngresso;

    // Matrícula
    private Long turmaId;

    @NotNull(message = "Consentimento LGPD é obrigatório")
    @AssertTrue(message = "É necessário aceitar os termos LGPD")
    private Boolean lgpdConsentimentoGeral;
}
