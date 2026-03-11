package com.waldorf.application.dto.turma;

import java.time.LocalDateTime;

public record TurmaResponseDTO(
        Long id,
        String nome,
        Integer anoLetivo,
        Long professorRegenteId,
        String professorRegentNome,
        int totalAlunos,
        boolean ativa,
        LocalDateTime createdAt
) {}
