package com.waldorf.application.dto.observacao;

import com.waldorf.domain.enums.AspectoDesenvolvimento;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ObservacaoResponseDTO(
        Long id,
        Long alunoId,
        String alunoNome,
        Long professorId,
        String professorNome,
        AspectoDesenvolvimento aspecto,
        String conteudo,
        boolean privada,
        LocalDate data,
        LocalDateTime createdAt
) {}
