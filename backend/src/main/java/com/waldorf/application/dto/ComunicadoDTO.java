package com.waldorf.application.dto;

public record ComunicadoDTO(
        Long id,
        String assunto,
        String corpo,
        String destinatarios,
        Long turmaId,
        String turmaNome,
        String autorNome,
        String dataEnvio,
        Integer totalDestinatarios,
        Integer totalLidos
) {}
