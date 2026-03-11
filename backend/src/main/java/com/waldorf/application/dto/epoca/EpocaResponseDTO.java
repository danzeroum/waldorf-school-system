package com.waldorf.application.dto.epoca;

import com.waldorf.domain.enums.AspectoDesenvolvimento;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EpocaResponseDTO(
        Long id,
        Long turmaId,
        String turmaNome,
        String titulo,
        String materia,
        AspectoDesenvolvimento aspecto,
        LocalDate dataInicio,
        LocalDate dataFim,
        String descricao,
        String objetivos,
        String status,
        LocalDateTime createdAt
) {}
