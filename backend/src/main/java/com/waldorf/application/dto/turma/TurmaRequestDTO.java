package com.waldorf.application.dto.turma;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TurmaRequestDTO(
        @NotBlank String nome,
        @NotNull Integer anoLetivo,
        @NotNull Integer anoEscolar,
        Long professorId,
        Integer capacidadeMaxima
) {}
