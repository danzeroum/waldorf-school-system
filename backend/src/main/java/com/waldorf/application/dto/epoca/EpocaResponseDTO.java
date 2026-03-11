package com.waldorf.application.dto.epoca;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EpocaResponseDTO(
        Long id,
        Long turmaId,
        String turmaNome,
        String titulo,
        String materia,
        String aspecto,
        LocalDate dataInicio,
        LocalDate dataFim,
        String descricao,
        String objetivos,
        String status,
        LocalDateTime createdAt
) {}
