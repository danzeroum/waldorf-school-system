package com.waldorf.application.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaRequestDTO(
    @NotBlank(message = "Senha atual e obrigatoria")
    String senhaAtual,
    @NotBlank(message = "Nova senha e obrigatoria")
    @Size(min = 6, max = 100, message = "Nova senha deve ter entre 6 e 100 caracteres")
    String novaSenha
) {}
