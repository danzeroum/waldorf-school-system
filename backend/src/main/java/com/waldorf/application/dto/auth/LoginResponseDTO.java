package com.waldorf.application.dto.auth;

public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        UsuarioResponseDTO usuario
) {}
