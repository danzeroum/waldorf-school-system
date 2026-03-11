package com.waldorf.application.dto.observacao;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ObservacaoResponseDTO(
        Long id,
        Long alunoId,
        String alunoNome,
        Long professorId,
        String professorNome,
        String aspecto,
        String conteudo,
        boolean privada,
        LocalDate data,
        LocalDateTime createdAt
) {}
