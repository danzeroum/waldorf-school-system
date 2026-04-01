package com.waldorf.application.dto.financeiro;

import com.waldorf.domain.entity.Mensalidade.StatusMensalidade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MensalidadeResponseDTO(
        Long id,
        Long contratoId,
        String alunoNome,
        Integer numero,
        String descricao,
        BigDecimal valor,
        BigDecimal valorPago,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        StatusMensalidade status,
        String formaPagamento,
        String observacao,
        LocalDateTime createdAt
) {}
