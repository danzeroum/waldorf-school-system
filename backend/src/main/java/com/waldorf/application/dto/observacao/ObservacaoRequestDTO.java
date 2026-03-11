package com.waldorf.application.dto.observacao;

import com.waldorf.domain.enums.AspectoDesenvolvimento;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ObservacaoRequestDTO(
        @NotNull Long alunoId,
        @NotNull Long professorId,
        @NotNull AspectoDesenvolvimento aspecto,
        @NotBlank @Size(min = 10) String conteudo,
        boolean privada,
        @NotNull LocalDate data
) {}
