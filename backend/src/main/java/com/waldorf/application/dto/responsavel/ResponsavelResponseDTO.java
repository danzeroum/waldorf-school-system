package com.waldorf.application.dto.responsavel;

import com.waldorf.domain.enums.Genero;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ResponsavelResponseDTO(
        Long id,
        String nome,
        LocalDate dataNascimento,
        Genero genero,
        String email,
        String telefone,
        String profissao,
        String empresa,
        boolean autorizado,
        List<AlunoResumoDTO> alunos,
        LocalDateTime createdAt
) {
    public record AlunoResumoDTO(Long id, String nome, String matricula) {}
}
