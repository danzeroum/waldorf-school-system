package com.waldorf.application.dto.financeiro;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContratoRequestDTO(
        @NotNull Long alunoId,
        Long responsavelId,
        @NotNull Integer anoLetivo,
        @NotNull @Positive BigDecimal valorMensalidade,
        BigDecimal valorMatricula,
        @NotNull @Positive Integer totalParcelas,
        @NotNull Integer diaVencimento,
        @NotNull LocalDate dataInicio,
        String observacoes,
        String desconto
) {}
