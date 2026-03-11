package com.waldorf.application.dto.observacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ObservacaoRequestDTO(
        @NotNull Long alunoId,
        @NotNull Long professorId,
        @NotBlank String aspecto,
        @NotBlank String conteudo,
        boolean privada,
        @NotNull LocalDate data
) {}
