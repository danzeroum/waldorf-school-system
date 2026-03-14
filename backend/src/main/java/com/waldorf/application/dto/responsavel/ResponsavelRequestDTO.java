package com.waldorf.application.dto.responsavel;

import com.waldorf.domain.enums.Genero;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record ResponsavelRequestDTO(
        @NotBlank String nome,
        LocalDate dataNascimento,
        Genero genero,
        @Email String email,
        String telefone,
        String cpf,
        String parentesco,
        String profissao,
        String empresa,
        boolean autorizado
) {}
