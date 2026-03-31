package com.waldorf.application.dto;

public record SolicitacaoDTO(
        Long id,
        String tipo,
        String status,
        String descricao,
        String resposta,
        String prazo,
        String createdAt
) {}
