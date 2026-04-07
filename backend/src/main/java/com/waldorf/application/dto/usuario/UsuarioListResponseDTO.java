package com.waldorf.application.dto.usuario;

import java.time.LocalDateTime;
import java.util.Set;

public record UsuarioListResponseDTO(
    Long id,
    String nome,
    String email,
    boolean ativo,
    Set<String> perfis,
    LocalDateTime createdAt
) {}
