package br.edu.waldorf.modules.pessoa.api.dto;

import br.edu.waldorf.modules.pessoa.domain.model.Aluno;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta para Aluno
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlunoResponseDTO {

    private Long id;
    private String numeroMatricula;
    private String nomeCompleto;
    private String nomeSocial;
    private String cpf;
    private LocalDate dataNascimento;
    private String email;
    private String telefonePrincipal;
    private String fotoUrl;
    private Aluno.SituacaoAluno situacao;

    // Dados médicos (somente para roles autorizadas)
    private String tipoSanguineo;
    private String planoSaude;
    private String alergias;
    private String necessidadesEspeciais;

    // Dados Waldorf
    private String temperamento;
    private String naturalidade;
    private String nomePai;
    private String nomeMae;
    private Integer anoIngresso;

    // Turma
    private Long turmaId;
    private String turmaNome;

    // Auditoria
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
