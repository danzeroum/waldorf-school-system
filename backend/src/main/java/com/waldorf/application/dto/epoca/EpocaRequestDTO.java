package com.waldorf.application.dto.epoca;

import com.waldorf.domain.enums.AspectoDesenvolvimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record EpocaRequestDTO(
        @NotNull Long turmaId,
        @NotBlank String titulo,
        @NotBlank String materia,
        @NotNull AspectoDesenvolvimento aspecto,
        @NotNull LocalDate dataInicio,
        @NotNull LocalDate dataFim,
        String descricao,
        String objetivos
) {}
