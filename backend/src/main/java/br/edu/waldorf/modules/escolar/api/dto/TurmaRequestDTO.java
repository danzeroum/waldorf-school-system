package br.edu.waldorf.modules.escolar.api.dto;

import br.edu.waldorf.modules.escolar.domain.model.Turma;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO de requisição para Turma
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurmaRequestDTO {

    @NotBlank(message = "Código é obrigatório")
    @Size(max = 20)
    private String codigo;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    private String nome;

    @NotNull(message = "Curso é obrigatório")
    private Long cursoId;

    @NotNull(message = "Ano letivo é obrigatório")
    private Integer anoLetivo;

    @NotNull(message = "Série é obrigatória")
    private Integer serie;

    private Turma.Turno turno;
    private String sala;

    @Min(1) @Max(50)
    private Integer capacidadeMaxima;

    private Long professorTitularId;
    private Long professorAuxiliarId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String corTurma;
}
