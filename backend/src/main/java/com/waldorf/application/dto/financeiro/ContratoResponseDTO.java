package com.waldorf.application.dto.financeiro;

import com.waldorf.domain.enums.SituacaoContrato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContratoResponseDTO(
        Long id,
        Long alunoId,
        String alunoNome,
        int anoLetivo,
        BigDecimal valorMensalidade,
        BigDecimal valorMatricula,
        int totalParcelas,
        int diaVencimento,
        LocalDate dataInicio,
        SituacaoContrato situacao,
        LocalDateTime createdAt
) {}
