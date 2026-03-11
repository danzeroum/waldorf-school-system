package com.waldorf.application.dto.aluno;

import com.waldorf.domain.enums.Genero;
import com.waldorf.domain.enums.Temperamento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record AlunoResponseDTO(
        Long id,
        String matricula,
        String nome,
        LocalDate dataNascimento,
        Genero genero,
        String email,
        String telefone,
        Long turmaId,
        String turmaNome,
        Integer anoIngresso,
        Temperamento temperamento,
        boolean ativo,
        List<ResponsavelResumoDTO> responsaveis,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record ResponsavelResumoDTO(
            Long id,
            String nome,
            String parentesco,
            String telefone,
            String email
    ) {}
}
