package com.waldorf.application.dto;

public record PreferenciaNotificacaoDTO(
        Long id,
        boolean email,
        boolean push,
        boolean sms,
        boolean inApp,
        String agregacao,
        String silencioInicio,
        String silencioFim
) {}
