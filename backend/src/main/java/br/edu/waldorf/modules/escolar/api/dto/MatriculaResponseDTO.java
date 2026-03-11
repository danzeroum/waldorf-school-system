package br.edu.waldorf.modules.escolar.api.dto;

import br.edu.waldorf.modules.escolar.domain.model.Matricula;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta para Matrícula
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatriculaResponseDTO {

    private Long id;
    private String numeroMatricula;
    private Long alunoId;
    private String alunoNome;
    private Long turmaId;
    private String turmaNome;
    private Integer anoLetivo;
    private LocalDate dataMatricula;
    private LocalDate dataCancelamento;
    private String motivoCancelamento;
    private Matricula.FormaIngresso formaIngresso;
    private Matricula.TipoEnsino tipoEnsino;
    private Matricula.SituacaoMatricula situacao;
    private BigDecimal mediaFinal;
    private BigDecimal frequenciaFinal;
    private String observacoes;
    private LocalDateTime createdAt;
}
