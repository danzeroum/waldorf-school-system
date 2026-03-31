package com.waldorf.application.dto;

public record ConsentimentoDTO(
        Long id,
        Long alunoId,
        String alunoNome,
        String responsavelNome,
        String responsavelEmail,
        String tipo,
        String status,
        String dataAceite,
        String dataRevogacao,
        String versaoTermos
) {}
