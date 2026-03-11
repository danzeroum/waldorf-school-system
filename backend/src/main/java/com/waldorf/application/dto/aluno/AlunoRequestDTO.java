package com.waldorf.application.dto.aluno;

import com.waldorf.domain.enums.Genero;
import com.waldorf.domain.enums.Temperamento;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record AlunoRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 150)
        String nome,

        @NotNull
        LocalDate dataNascimento,

        @NotNull
        Genero genero,

        @Email
        String email,

        String telefone,

        Long turmaId,

        @NotNull
        Integer anoIngresso,

        Temperamento temperamento,

        // Endereço
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado
) {}
