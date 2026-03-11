package com.waldorf.application.dto.responsavel;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResponsavelRequestDTO(
        @NotBlank String nome,
        @NotBlank String cpf,
        @Email String email,
        String telefone,
        String parentesco,
        boolean responsavelFinanceiro,
        String enderecoRua,
        String enderecoNumero,
        String enderecoBairro,
        String enderecoCidade,
        String enderecoEstado,
        String enderecoCep
) {}
