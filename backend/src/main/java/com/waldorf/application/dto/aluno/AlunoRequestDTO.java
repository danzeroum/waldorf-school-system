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
        Long turmaId,
        int anoIngresso,
        String temperamento
) {}
