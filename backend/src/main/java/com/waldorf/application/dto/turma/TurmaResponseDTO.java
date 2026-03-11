package com.waldorf.application.dto.turma;

import java.time.LocalDateTime;

public record TurmaResponseDTO(
        Long id,
        String nome,
        Integer anoLetivo,
        Integer anoEscolar,
        Long professorId,
        String professorNome,
        Integer capacidadeMaxima,
        Integer totalAlunos,
        LocalDateTime createdAt
) {}
