package com.waldorf.application.dto.auth;

import java.util.Set;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        Set<String> perfis
) {}
