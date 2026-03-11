package br.edu.waldorf.modules.pessoa.api.dto;

import br.edu.waldorf.modules.pessoa.domain.model.Professor;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta para Professor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessorResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String email;
    private String telefonePrincipal;
    private String fotoUrl;
    private String registroProfissional;
    private String formacao;
    private String especializacaoWaldorf;
    private Integer anoFormacaoWaldorf;
    private String biografia;
    private Professor.SituacaoProfessor situacao;
    private LocalDate dataAdmissao;
    private LocalDateTime createdAt;
}
