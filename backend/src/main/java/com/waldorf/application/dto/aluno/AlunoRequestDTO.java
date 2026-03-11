package com.waldorf.application.dto.aluno;

import com.waldorf.domain.enums.Genero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AlunoRequestDTO(
        @NotBlank String nome,
        @NotNull LocalDate dataNascimento,
        Genero genero,
        String email,
        String telefone,
        String cpf,
        @NotNull Integer anoIngresso,
        Long turmaId,
        String rua,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep
) {}
