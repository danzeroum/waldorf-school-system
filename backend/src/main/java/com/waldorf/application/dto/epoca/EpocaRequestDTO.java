package com.waldorf.application.dto.epoca;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EpocaRequestDTO(
        @NotNull Long turmaId,
        @NotBlank String titulo,
        @NotBlank String materia,
        String aspecto,
        @NotNull LocalDate dataInicio,
        LocalDate dataFim,
        String descricao,
        String objetivos
) {}
