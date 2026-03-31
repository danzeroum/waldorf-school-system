package com.waldorf.application.dto;

public record NotificacaoDTO(
        Long id,
        String tipo,
        String titulo,
        String mensagem,
        Long referenciaId,
        String referenciaTipo,
        boolean lida,
        String lidaEm,
        String createdAt
) {}
