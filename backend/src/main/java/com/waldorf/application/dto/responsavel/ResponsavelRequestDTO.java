package com.waldorf.application.dto.responsavel;

import com.waldorf.domain.enums.Genero;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ResponsavelRequestDTO(
        @NotBlank @Size(min = 3, max = 150) String nome,
        @NotNull LocalDate dataNascimento,
        @NotNull Genero genero,
        @Email String email,
        @NotBlank String telefone,
        String cpf,
        String profissao,
        String empresa,
        boolean autorizado,
        // Endereço
        String cep,
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        String estado
) {}
