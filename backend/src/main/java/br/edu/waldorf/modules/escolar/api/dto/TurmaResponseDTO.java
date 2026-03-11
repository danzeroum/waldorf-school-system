package br.edu.waldorf.modules.escolar.api.dto;

import br.edu.waldorf.modules.escolar.domain.model.Turma;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta para Turma
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurmaResponseDTO {

    private Long id;
    private String codigo;
    private String nome;
    private Long cursoId;
    private String cursoNome;
    private Integer anoLetivo;
    private Integer serie;
    private Turma.Turno turno;
    private String sala;
    private Integer capacidadeMaxima;
    private Integer vagasDisponiveis;
    private Long professorTitularId;
    private String professorTitularNome;
    private Long professorAuxiliarId;
    private String professorAuxiliarNome;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Turma.SituacaoTurma situacao;
    private String corTurma;
    private Long totalAlunos;
    private LocalDateTime createdAt;
}
