package com.waldorf.application.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record UsuarioRequestDTO(
    @NotBlank(message = "Nome e obrigatorio")
    @Size(min = 3, max = 200, message = "Nome deve ter entre 3 e 200 caracteres")
    String nome,
    @NotBlank(message = "E-mail e obrigatorio")
    @Email(message = "E-mail invalido")
    String email,
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    String senha,
    Set<String> perfis,
    Boolean ativo
) {}
