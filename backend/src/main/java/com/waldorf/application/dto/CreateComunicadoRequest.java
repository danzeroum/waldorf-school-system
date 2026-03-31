package com.waldorf.application.dto;

public record CreateComunicadoRequest(
        String assunto,
        String corpo,
        String destinatarios,
        Long turmaId
) {}
