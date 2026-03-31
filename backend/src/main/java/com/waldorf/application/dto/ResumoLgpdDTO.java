package com.waldorf.application.dto;

public record ResumoLgpdDTO(
        long totalConsentimentos,
        long consentimentosAtivos,
        long consentimentosPendentes,
        long consentimentosRevogados,
        long solicitacoesPendentes,
        long solicitacoesEmAnalise,
        int percentualConformidade
) {}
