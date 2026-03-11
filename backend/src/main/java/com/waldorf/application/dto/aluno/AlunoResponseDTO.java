package com.waldorf.application.dto.aluno;

import com.waldorf.domain.enums.Genero;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AlunoResponseDTO(
        Long id,
        String matricula,
        String nome,
        LocalDate dataNascimento,
        Genero genero,
        String email,
        int anoIngresso,
        String turmaNome,
        String temperamento,
        boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
