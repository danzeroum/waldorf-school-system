package com.waldorf.application.dto.financeiro;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContratoRequestDTO(
        @NotNull Long alunoId,
        Long responsavelFinanceiroId,
        @NotNull Integer anoLetivo,
        @NotNull @Positive BigDecimal valorMensalidade,
        BigDecimal desconto,
        @NotNull Integer totalParcelas,
        Integer diaVencimento,
        @NotNull LocalDate dataInicio,
        LocalDate dataFim,
        String observacoes
) {}
