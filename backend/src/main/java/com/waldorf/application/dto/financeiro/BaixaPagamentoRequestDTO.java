package com.waldorf.application.dto.financeiro;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BaixaPagamentoRequestDTO(
        @NotNull @DecimalMin("0.01") BigDecimal valorPago,
        @NotNull LocalDate dataPagamento,
        @NotBlank String formaPagamento,
        String observacao
) {}
