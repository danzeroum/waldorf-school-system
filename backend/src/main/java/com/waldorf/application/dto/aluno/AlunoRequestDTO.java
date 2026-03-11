package com.waldorf.application.dto.aluno;

import com.waldorf.domain.enums.Genero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * 15 campos — ordem exata esperada pelos testes de integração e unitários:
 * nome, dataNascimento, genero, email, telefone, turmaId, anoIngresso, temperamento,
 * enderecoRua, enderecoNumero, enderecoBairro, enderecoCidade, enderecoEstado, enderecoCep, observacoes
 */
public record AlunoRequestDTO(
        @NotBlank String nome,
        @NotNull LocalDate dataNascimento,
        Genero genero,
        String email,
        String telefone,
        Long turmaId,
        int anoIngresso,
        String temperamento,
        String enderecoRua,
        String enderecoNumero,
        String enderecoBairro,
        String enderecoCidade,
        String enderecoEstado,
        String enderecoCep,
        String observacoes
) {}
