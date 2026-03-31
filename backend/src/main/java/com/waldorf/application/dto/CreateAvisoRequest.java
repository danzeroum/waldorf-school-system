package com.waldorf.application.dto;

public record CreateAvisoRequest(
        String titulo,
        String conteudo,
        String tipo,
        Long turmaId,
        boolean fixado,
        String dataExpiracao
) {}
