package com.waldorf.application.dto.financeiro;

import java.math.BigDecimal;

public record ResumoFinanceiroDTO(
        BigDecimal totalReceita,
        BigDecimal totalRecebido,
        BigDecimal totalPendente,
        BigDecimal totalVencido,
        long totalContratos,
        long inadimplentes,
        double taxaInadimplencia
) {}
