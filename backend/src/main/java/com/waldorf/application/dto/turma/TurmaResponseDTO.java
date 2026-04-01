package com.waldorf.application.dto.turma;

import java.time.LocalDateTime;

public record TurmaResponseDTO(
        Long id,
        String nome,
        Integer anoLetivo,
        Integer anoEscolar,
        Integer capacidadeMaxima,
        Long professorRegenteId,
        String professorRegenteNome,
        int totalAlunos,
        boolean ativa,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
