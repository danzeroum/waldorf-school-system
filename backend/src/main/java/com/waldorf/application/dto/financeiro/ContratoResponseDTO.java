package com.waldorf.application.dto.financeiro;

import com.waldorf.domain.enums.SituacaoContrato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContratoResponseDTO(
        Long id,
        Long alunoId,
        String alunoNome,
        Integer anoLetivo,
        BigDecimal valorMensalidade,
        BigDecimal desconto,
        Integer totalParcelas,
        Integer diaVencimento,
        LocalDate dataInicio,
        LocalDate dataFim,
        SituacaoContrato situacao,
        LocalDateTime createdAt
) {}
