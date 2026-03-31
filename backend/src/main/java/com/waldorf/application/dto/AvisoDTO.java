package com.waldorf.application.dto;

public record AvisoDTO(
        Long id,
        String titulo,
        String conteudo,
        String tipo,
        Long turmaId,
        String turmaNome,
        String autorNome,
        boolean fixado,
        String dataPublicacao,
        String dataExpiracao
) {}
