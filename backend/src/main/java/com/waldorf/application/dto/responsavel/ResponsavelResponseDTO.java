package com.waldorf.application.dto.responsavel;

import java.time.LocalDateTime;

public record ResponsavelResponseDTO(
        Long id,
        String nome,
        String cpf,
        String email,
        String telefone,
        String parentesco,
        boolean responsavelFinanceiro,
        LocalDateTime createdAt
) {}
